package cn.fd.ratziel.core.coroutine.task

import cn.fd.ratziel.core.task.TaskLifeCycle
import cn.fd.ratziel.core.util.randomUUID
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Function
import kotlin.coroutines.suspendCoroutine

/**
 * ContinuousTaskController
 * 延续性任务控制器
 *
 * @author TheFloodDragon
 * @since 2023/9/9 20:24
 */
open class ContinuousTaskController<T> {

    /**
     * 运行中的任务
     */
    val runningTasks: ConcurrentHashMap<String, ContinuousTask<T>> = ConcurrentHashMap()

    /**
     * 提交任务
     * @param id 任务ID
     * @param task ContinuousTask
     */
    open fun submit(id: String, task: ContinuousTask<T>) {
        runningTasks[id] = task
    }

    /**
     * 完成任务
     * @param id 任务ID
     * @param result 任务的返回值
     */
    open fun complete(id: String, result: T) {
        runningTasks[id]?.completeWith(result)
        runningTasks.remove(id)
    }

    /**
     * 清除所有已完成的任务
     */
    open fun clearCache() {
        runningTasks.values.forEach { if (it.isFinished) runningTasks.remove(it.id) }
    }

    /**
     * 获取所有任务
     */
    open fun getTasks(): Collection<ContinuousTask<T>> = runningTasks.values

    /**
     * 获取所有任务ID
     */
    open fun getIds(): List<String> = runningTasks.map { it.key }

    /**
     * 新建任务
     */
    suspend inline fun newTask(
        id: String = randomUUID(),
        lifeCycle: TaskLifeCycle = defaultTaskLifeCycle(this, id),
        runner: Function<ContinuousTask<T>, Unit> = Function { },
    ) = newContinuousTask(id, this, lifeCycle, runner)

    companion object {

        /**
         * 默认任务行迹
         * 任务完成自动移除该任务
         */
        fun <T> defaultTaskLifeCycle(controller: ContinuousTaskController<T>, id: String) = TaskLifeCycle(
            onFinish = {
                controller.runningTasks.remove(id)
            })

        /**
         * 新建一个延续性任务
         * @param id 任务ID
         * @param controller 任务控制器
         * @param runner 任务运行内容
         */
        suspend inline fun <T> newContinuousTask(
            id: String = randomUUID(),
            controller: ContinuousTaskController<T>,
            lifeCycle: TaskLifeCycle = defaultTaskLifeCycle(controller, id),
            runner: Function<ContinuousTask<T>, Unit> = Function { },
        ) = suspendCoroutine {
            ContinuousTask(id, it, lifeCycle)
                .let { task ->
                    controller.submit(id, task)
                    runner.apply(task)
                }
        }

    }

}
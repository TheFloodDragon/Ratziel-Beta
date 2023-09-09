package cn.fd.ratziel.core.coroutine.task

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
class ContinuousTaskController<T> {

    /**
     * 运行中的任务
     */
    private val runningTasks: ConcurrentHashMap<String, ContinuousTask<T>> = ConcurrentHashMap()

    /**
     * 提交任务
     * @param id 任务ID
     * @param task ContinuousTask
     */
    fun submit(id: String, task: ContinuousTask<T>) {
        runningTasks[id] = task
    }

    /**
     * 完成任务
     * @param id 任务ID
     * @param result 任务的返回值
     */
    fun complete(id: String, result: T) {
        runningTasks[id]?.completeWith(result)
        runningTasks.remove(id)
    }

    /**
     * 获取未完成的任务
     */
    fun getRunningTasks() = runningTasks

    /**
     * 获取所有任务
     */
    fun getTasks(): Collection<ContinuousTask<T>> = runningTasks.values

    /**
     * 获取所有任务ID
     */
    fun getIds(): List<String> = runningTasks.map { it.key }

    /**
     * 新建任务
     */
    suspend inline fun newTask(
        id: String = randomUUID(),
        runner: Function<ContinuousTask<T>, Unit> = Function { },
    ) = newContinuousTask(id, this, runner)

    companion object {

        /**
         * 新建一个延续性任务
         * @param id 任务ID
         * @param controller 任务控制器
         * @param runner 任务运行内容
         */
        suspend inline fun <T> newContinuousTask(
            id: String = randomUUID(),
            controller: ContinuousTaskController<T>,
            runner: Function<ContinuousTask<T>, Unit> = Function { },
        ) = suspendCoroutine {
            ContinuousTask(id, it)
                .let { task ->
                    controller.submit(id, task)
                    runner.apply(task)
                }
        }

    }

}
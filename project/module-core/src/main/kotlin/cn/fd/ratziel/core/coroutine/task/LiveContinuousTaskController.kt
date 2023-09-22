package cn.fd.ratziel.core.coroutine.task

import cn.fd.ratziel.core.task.TaskLifeTrace
import cn.fd.ratziel.core.util.randomUUID
import java.util.function.Function
import kotlin.coroutines.suspendCoroutine
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * LiveContinuousTaskController
 * 有生命的延续性任务的控制器
 *
 * @author TheFloodDragon
 * @since 2023/9/10 12:26
 */
open class LiveContinuousTaskController<T> : ContinuousTaskController<T>() {

    /**
     * 获取所有任务
     */
    open fun getLiveTasks(): List<LiveContinuousTask<T>> = getTasks().filterIsInstance<LiveContinuousTask<T>>()

    /**
     * 新建任务
     */
    suspend inline fun newTask(
        id: String = randomUUID(),
        duration: Duration,
        timeoutResult: T,
        taskLifeTrace: TaskLifeTrace = defaultTaskLifeTrace(this, id),
        runner: Function<LiveContinuousTask<T>, Unit> = Function { },
    ) = newContinuousTask(id, this, duration, timeoutResult, taskLifeTrace, runner)

    suspend inline fun newTask(
        id: String = randomUUID(),
        duration: Long,
        timeoutResult: T,
        taskLifeTrace: TaskLifeTrace = defaultTaskLifeTrace(this, id),
        runner: Function<LiveContinuousTask<T>, Unit> = Function { },
    ) = newTask(id, duration.milliseconds, timeoutResult, taskLifeTrace, runner)

    companion object {

        /**
         * 新建一个延续性任务
         * @param id 任务ID
         * @param controller 任务控制器
         * @param duration 任务持续时间
         * @param timeoutResult 生命死亡后(超时)未返回值时的默认返回值
         * @param taskLifeTrace 任务行迹
         * @param runner 任务运行内容
         */
        suspend inline fun <T> newContinuousTask(
            id: String = randomUUID(),
            controller: LiveContinuousTaskController<T>,
            duration: Duration,
            timeoutResult: T,
            taskLifeTrace: TaskLifeTrace = defaultTaskLifeTrace(controller, id),
            runner: Function<LiveContinuousTask<T>, Unit> = Function { },
        ) = suspendCoroutine {
            LiveContinuousTask(id, it, duration, timeoutResult, taskLifeTrace)
                .let { task ->
                    controller.submit(id, task)
                    runner.apply(task)
                }
        }

    }

}
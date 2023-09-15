package cn.fd.ratziel.core.coroutine.task

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
        defaultResult: T,
        runner: Function<LiveContinuousTask<T>, Unit> = Function { },
    ) = newContinuousTask(id, this, duration, defaultResult, runner)

    suspend inline fun newTask(
        id: String = randomUUID(),
        duration: Long,
        defaultResult: T,
        runner: Function<LiveContinuousTask<T>, Unit> = Function { },
    ) = newContinuousTask(id, this, duration, defaultResult, runner)

    companion object {

        /**
         * 新建一个延续性任务
         * @param id 任务ID
         * @param controller 任务控制器
         * @param duration 任务持续时间
         * @param defaultResult 生命结束后的默认返回值
         * @param runner 任务运行内容
         */
        suspend inline fun <T> newContinuousTask(
            id: String = randomUUID(),
            controller: LiveContinuousTaskController<T>,
            duration: Duration,
            defaultResult: T,
            runner: Function<LiveContinuousTask<T>, Unit> = Function { },
        ) = suspendCoroutine {
            LiveContinuousTask(id, it, duration, defaultResult)
                .let { task ->
                    controller.submit(id, task)
                    runner.apply(task)
                }
        }

        suspend inline fun <T> newContinuousTask(
            id: String = randomUUID(),
            controller: LiveContinuousTaskController<T>,
            duration: Long,
            defaultResult: T,
            runner: Function<LiveContinuousTask<T>, Unit> = Function { },
        ) = newContinuousTask(id, controller, duration.milliseconds, defaultResult, runner)

    }

}
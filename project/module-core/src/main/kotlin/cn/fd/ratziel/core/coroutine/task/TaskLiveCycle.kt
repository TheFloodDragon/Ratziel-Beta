package cn.fd.ratziel.core.coroutine.task

import java.util.function.Function

/**
 * 任务的生命周期
 *
 * @author TheFloodDragon
 * @since 2023/9/22 21:44
 */
open class TaskLiveCycle<T>(
    /**
     * 任务运行前
     */
    protected val beforeStart: Function<TaskLiveCycle<T>, Unit> = Function {},
    /**
     * 任务运行时
     */
    protected val onStart: Function<TaskLiveCycle<T>, Unit> = Function {},
    /**
     * 任务结束前
     */
    protected val beforeFinish: Function<TaskLiveCycle<T>, Unit> = Function {},
    /**
     * 任务结束后
     */
    protected val onFinish: Function<TaskLiveCycle<T>, Unit> = Function {},
) {

    /**
     * 任务是否开始
     */
    var isStarted = false
        protected set

    /**
     * 任务是否结束
     */
    var isFinished = false
        protected set

    /**
     * 开始任务
     */
    protected open fun startTask() {
        beforeStart.apply(this)
        isStarted = true
        onStart.apply(this)
    }

    /**
     * 结束任务
     */
    protected open fun finishTask() {
        beforeFinish.apply(this)
        isFinished = true
        onFinish.apply(this)
    }

}
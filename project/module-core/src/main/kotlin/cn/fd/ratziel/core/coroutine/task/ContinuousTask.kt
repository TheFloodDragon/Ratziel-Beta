package cn.fd.ratziel.core.coroutine.task

import cn.fd.ratziel.core.Task
import cn.fd.ratziel.core.coroutine.task.TaskLiveCycle
import kotlin.coroutines.*

/**
 * ContinuousTask
 * 延续性任务
 *
 * @author TheFloodDragon
 * @since 2023/9/9 20:26
 */
open class ContinuousTask<T>(
    /**
     * 任务ID
     */
    override val id: String,
    /**
     * Kotlin Continuation
     */
    private val ctn: Continuation<T>,
    /**
     * 是否立马开始任务
     */
    immediate: Boolean = false,
) : TaskLiveCycle<T>(), Task {

    init {
        if (immediate) start()
    }


    /**
     * 开始任务
     */
    fun start() = startTask()

    /**
     * 完成任务
     */
    open fun completeWith(result: T) {
        beforeFinish.apply(this)
        // 恢复
        ctn.resume(result)
        // 结束
        isFinished = true
        onFinish.apply(this)
    }

    open fun forceFinish() {
        beforeFinish.apply(this)
        // 带异常恢复
        ctn.resumeWithException(TaskForceFinishedException(this))
        // 结束
        isFinished = true
        onFinish.apply(this)
    }

}
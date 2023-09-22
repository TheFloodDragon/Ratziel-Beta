package cn.fd.ratziel.core.coroutine.task

import cn.fd.ratziel.core.task.BaseTask
import cn.fd.ratziel.core.task.TaskForceFinishedException
import cn.fd.ratziel.core.task.TaskLifeTrace
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

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
    id: String,
    /**
     * Kotlin Continuation
     */
    private val ctn: Continuation<T>,
    /**
     * 是否立马开始任务
     */
    immediate: Boolean = false,
    /**
     * 任务行迹
     */
    taskLiveCycle: TaskLifeTrace = TaskLifeTrace(),
) : BaseTask(id, taskLiveCycle) {

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
        finishTask {
            ctn.resume(result)
        }
    }

    open fun forceFinish() {
        finishTask {
            ctn.resumeWithException(TaskForceFinishedException(this))
        }
    }

}
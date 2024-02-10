package cn.fd.ratziel.core.unuse.coroutine.task

import cn.fd.ratziel.core.unuse.task.BaseTask
import cn.fd.ratziel.core.unuse.task.exception.TaskForceFinishedException
import cn.fd.ratziel.core.unuse.task.TaskLifeCycle
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
     * 延续者
     */
    private val continuator: Continuation<T>,
    /**
     * 任务行迹
     */
    lifeCycle: TaskLifeCycle = TaskLifeCycle(),
) : BaseTask(id, lifeCycle) {

    /**
     * 开始任务
     */
    open fun start() = startTask()

    /**
     * 完成任务
     */
    open fun completeWith(result: T) {
        finishTask {
            continuator.resume(result)
        }
    }

    open fun forceFinish() {
        finishTask {
            continuator.resumeWithException(TaskForceFinishedException(this))
        }
    }

}
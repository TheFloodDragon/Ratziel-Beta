package cn.fd.ratziel.core.coroutine.task

import cn.fd.ratziel.core.Task
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

/**
 * ContinuousTask
 * 延续性任务
 *
 * @author TheFloodDragon
 * @since 2023/9/9 20:26
 */
open class ContinuousTask<T>(
    override val id: String,
    private val ctn: Continuation<T>,
) : Task {

    private var finished = false

    /**
     * 任务是否结束
     */
    override fun isFinished(): Boolean = finished

    /**
     * 完成任务
     */
    open fun completeWith(result: T) {
        finished = true
        ctn.resume(result)
    }

}
package cn.fd.ratziel.core.coroutine

import cn.fd.ratziel.core.util.randomUUID
import kotlin.coroutines.Continuation

/**
 * ContinuousTask TODO 完成这个
 *
 * @author TheFloodDragon
 * @since 2023/9/9 16:24
 */
class ContinuousTask(val ctn: Continuation<Any>) {

    private var complete: Boolean = false

    /**
     * 任务是否完成
     */
    fun isCompleted() = complete

    /**
     * 完成任务
     */
    fun <R : Any> completeWith(result: Result<R>) {
        complete = true
        ctn.resumeWith(result)
    }

    fun <R : Any> complete(result: R) {
        completeWith(Result.success(result))
    }

    companion object {
        val runningTasks: MutableMap<String, ContinuousTask> = mutableMapOf()


        fun <T> submit(task: ContinuousTask, taskID: String = randomUUID()) {
            runningTasks[taskID] = task
        }
    }

}
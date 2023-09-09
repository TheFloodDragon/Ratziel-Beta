package cn.fd.ratziel.core.coroutine

import cn.fd.ratziel.core.util.randomUUID
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Function
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * ContinuousTask
 *
 * @author TheFloodDragon
 * @since 2023/9/9 16:24
 */
class ContinuousTask(
    val taskID: String,
    private var ctn: Continuation<Any>,
) {

    init {
        submit(this)
    }

    private var complete: Boolean = false

    /**
     * 任务是否完成
     */
    fun isCompleted() = complete

    /**
     * 完成任务
     */
    private fun completeWith(result: Any) {
        complete = true
        ctn.resume(result)
        runningTasks.remove(taskID)
    }

    companion object {

        private val runningTasks: ConcurrentHashMap<String, ContinuousTask> = ConcurrentHashMap()

        /**
         * 根据任务ID完成任务
         */
        fun completeAll(fuzzyID: String, result: Any = Unit) =
            runningTasks.filter { it.key.startsWith(fuzzyID) }.onEach { it.value.completeWith(result) }

        fun completeConcrete(concreteTaskID: String, result: Any = Unit) {
            runningTasks[concreteTaskID]?.completeWith(result)
        }

        /**
         * 提交任务
         */
        fun submit(id: String, task: ContinuousTask) {
            runningTasks[id] = task
        }

        fun submit(task: ContinuousTask) {
            submit(task.taskID, task)
        }

        /**
         * 运行任务
         */
        suspend inline fun newContinuousTask(
            originalID: String,
            function: Function<ContinuousTask, Any> = Function { },
        ) = suspendCoroutine {
                ContinuousTask(formatID(originalID), it).let { task ->
                    submit(task)
                    function.apply(task)
                }
            }

        @Suppress("UNCHECKED_CAST")
        @JvmName("newContinuousTaskTyped")
        suspend inline fun <T> newContinuousTask(
            originalID: String,
            function: Function<ContinuousTask, Any> = Function { },
        ) = newContinuousTask(originalID, function) as T

        /**
         * 格式化ID
         * $originalID_$UUID
         */
        fun formatID(originalID: String) = StringBuilder(originalID).append(randomUUID()).toString()

    }

}
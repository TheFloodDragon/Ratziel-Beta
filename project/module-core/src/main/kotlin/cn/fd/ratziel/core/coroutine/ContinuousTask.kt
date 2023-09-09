package cn.fd.ratziel.core.coroutine

import cn.fd.ratziel.core.util.randomUUID
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
class ContinuousTask<in R>(
    private val ctn: Continuation<R>,
) {

    private var complete: Boolean = false

    /**
     * 任务是否完成
     */
    fun isCompleted() = complete

    /**
     * 完成任务
     */
    fun completeWith(result: R) {
        complete = true
        ctn.resume(result)
    }

    companion object {

        val runningTasks: MutableMap<String, ContinuousTask<*>> = mutableMapOf()

        /**
         * 提交任务
         */
        fun <T> submit(task: ContinuousTask<T>, taskID: String = randomUUID()) {
            runningTasks[taskID] = task
        }

        /**
         * 运行任务
         */
        suspend fun <T> newContinuousTask(function: Function<ContinuousTask<T>, Unit>) =
            suspendCoroutine {
                ContinuousTask(it).let { task ->
                    submit(task)
                    function.apply(task)
                }
            }
        suspend fun newContinuousTask(function: Function<ContinuousTask<Unit>, Unit>) =
            newContinuousTask<Unit>(function)

        /**
         * 完成所有任务
         */
        fun completeAll(){
//            runningTasks.values.forEach {
//                it.completeWith()
//            }
        }

    }

}
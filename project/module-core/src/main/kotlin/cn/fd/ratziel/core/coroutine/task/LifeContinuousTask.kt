package cn.fd.ratziel.core.coroutine.task

import cn.fd.ratziel.core.coroutine.ProxyCoroutineScopeIO
import cn.fd.ratziel.core.task.TaskLifeCycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.Continuation
import kotlin.time.Duration

/**
 * LiveContinuousTask
 * 有生命的延续性任务
 *
 * @author TheFloodDragon
 * @since 2023/9/10 12:19
 */
open class LifeContinuousTask<T>(
    id: String,
    continuator: Continuation<T>,
    /**
     * 生命持续时间
     */
    val duration: Duration,
    /**
     * 生命死亡后(超时)未返回值时的默认返回值
     */
    val timeoutResult: T,
    /**
     * 任务行迹
     */
    lifeCycle: TaskLifeCycle = TaskLifeCycle(),
) : ContinuousTask<T>(id, continuator, lifeCycle) {

    /**
     * 提供一个通用作用域
     */
    companion object : ProxyCoroutineScopeIO()

    /**
     * 是否死亡
     */
    var isDead = false
        private set

    /**
     * 是否存活
     */
    var isAlive = !isDead

    /**
     * 开始任务
     */
    override fun start() {
        super.start()
        waitToDie()
    }

    /**
     * 等待死亡
     */
    private fun waitToDie() {
        scope.launch {
            delay(duration)
            if (!isFinished) { // 防止重复取消
                isDead = true
                forceComplete()
            }
        }
    }

    /**
     * 强制完成任务(延续者将返回默认值)
     */
    open fun forceComplete() {
        return completeWith(timeoutResult)
    }

    /**
     * 重写以实现任务结束后修改生命状态
     */
    override fun finishTask(function: Runnable) {
        super.finishTask {
            function.run()
            isDead = true // 死亡
        }
    }

}
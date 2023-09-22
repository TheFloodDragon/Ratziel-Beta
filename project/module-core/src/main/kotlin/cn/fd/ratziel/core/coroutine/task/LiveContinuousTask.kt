package cn.fd.ratziel.core.coroutine.task

import cn.fd.ratziel.core.coroutine.ProxyCoroutineScopeIO
import cn.fd.ratziel.core.task.TaskLifeTrace
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
open class LiveContinuousTask<T>(
    id: String,
    continuator: Continuation<T>,
    /**
     * 生命持续时间
     */
    val duration: Duration,
    /**
     * 生命结束后的默认返回值
     */
    val defaultResult: T,
    /**
     * 是否立马开始任务
     */
    immediate: Boolean = false,
    /**
     * 任务行迹
     */
    taskLiveCycle: TaskLifeTrace = TaskLifeTrace(),
) : ContinuousTask<T>(id, continuator, false, taskLiveCycle) {

    /**
     * 注: 对父类的immediate设置成false是为了防止无法触发waitToDie()
     */
    init {
        if (immediate) this.start()
    }

    /**
     * 提供一个通用作用域
     */
    companion object : ProxyCoroutineScopeIO()

    /**
     * 是否死亡
     */
    var isDead = false
        private set

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
                complete()
            }
        }
    }

    /**
     * 完成任务
     */
    open fun complete() {
        return completeWith(defaultResult)
    }

}
package cn.fd.ratziel.core.coroutine.task

import cn.fd.ratziel.core.coroutine.ProxyCoroutineScopeIO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.Continuation
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * LiveContinuousTask
 * 有生命的延续性任务
 *
 * @author TheFloodDragon
 * @since 2023/9/10 12:19
 */
open class LiveContinuousTask<T>(
    id: String,
    ctn: Continuation<T>,
    /**
     * 生命持续时间
     */
    val duration: Duration,
    /**
     * 生命结束后的默认返回值
     */
    val defaultResult: T,
) : ContinuousTask<T>(id, ctn) {


    constructor(
        id: String,
        ctn: Continuation<T>,
        duration: Long,
        defaultResult: T,
    ) : this(id, ctn, duration.milliseconds, defaultResult)

    /**
     * 提供一个通用作用域
     */
    companion object : ProxyCoroutineScopeIO()

    /**
     * 生命结束标记
     */
    private var ended = false

    /**
     * 等待死亡
     */
    init {
        scope.launch {
            delay(duration)
            if (!isFinished()) { // 防止重复取消
                ended = true
                complete()
            }
        }
    }

    /**
     * 生命是否达到终点
     */
    open fun isReachedEnd() = ended || isFinished()

    /**
     * 完成任务
     */
    open fun complete() {
        return completeWith(defaultResult)
    }

}
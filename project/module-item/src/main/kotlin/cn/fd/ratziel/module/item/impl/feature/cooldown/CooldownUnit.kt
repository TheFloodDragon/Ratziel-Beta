@file:OptIn(ExperimentalUuidApi::class)

package cn.fd.ratziel.module.item.impl.feature.cooldown

import kotlin.time.Duration
import kotlin.time.TimeMark
import kotlin.time.TimeSource
import kotlin.uuid.ExperimentalUuidApi

/**
 * CooldownUnit
 *
 * @author TheFloodDragon
 * @since 2025/5/31 13:21
 */
class CooldownUnit(
    /**
     * 冷却标识 (名称)
     */
    val name: String,
) {

    /**
     * 冷却开始标记
     */
    @Volatile
    var startMark: TimeMark = TimeSource.Monotonic.markNow()
        private set

    /**
     * 冷却时长标记
     */
    @Volatile
    var durationMark: Duration = Duration.ZERO
        private set

    /**
     * 冷却结束标记
     */
    val endMark: TimeMark get() = startMark.plus(durationMark)

    /**
     * 是否正在冷却
     */
    val isInCooldown: Boolean get() = endMark.hasNotPassedNow()

    /**
     * 设置冷却时长
     */
    @Synchronized
    fun setCooldown(duration: Duration) {
        startMark = TimeSource.Monotonic.markNow()
        durationMark = duration
    }

    fun setCooldown(duration: String) = setCooldown(Duration.parse(duration))

    /**
     * 立刻结束冷却
     */
    @Synchronized
    fun stopImmediately() {
        // 立刻结束冷却，重置开始标记和持续时间
        durationMark = Duration.ZERO
        startMark = TimeSource.Monotonic.markNow()
    }

    /**
     * 增加冷却时长
     */
    @Synchronized
    fun increase(duration: Duration) {
        durationMark = durationMark + duration
    }

    fun increase(duration: String) = increase(Duration.parse(duration))

    /**
     * 减少冷却时长
     */
    @Synchronized
    fun decrease(duration: Duration) {
        durationMark = (durationMark - duration).coerceAtLeast(Duration.ZERO)
    }

    fun decrease(duration: String) = decrease(Duration.parse(duration))

}
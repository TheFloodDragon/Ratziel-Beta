@file:OptIn(ExperimentalUuidApi::class)

package cn.fd.ratziel.module.item.impl.feature.cooldown

import cn.fd.ratziel.module.item.api.ItemMaterial
import cn.fd.ratziel.module.item.api.NeoItem
import cn.fd.ratziel.module.item.impl.SimpleMaterial.Companion.toBukkit
import org.bukkit.Bukkit
import taboolib.module.nms.MinecraftVersion
import kotlin.time.Duration
import kotlin.time.TimeMark
import kotlin.time.TimeSource
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

/**
 * CooldownUnit
 *
 * @author TheFloodDragon
 * @since 2025/5/31 13:21
 */
class CooldownUnit(
    /**
     * 玩家 [Uuid]
     */
    val uuid: Uuid,
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
     * 冷却时长
     */
    @Volatile
    var interval: Duration = Duration.ZERO
        private set

    /**
     * 冷却结束标记
     */
    val endMark: TimeMark get() = startMark.plus(interval)

    /**
     * 是否正在冷却
     */
    val isInCooldown: Boolean get() = endMark.hasNotPassedNow()

    /**
     * 剩余冷却时间
     */
    val remaining: Duration get() = endMark.elapsedNow().absoluteValue

    /**
     * 设置冷却时长
     */
    @Synchronized
    fun setCooldown(interval: Duration) {
        this.startMark = TimeSource.Monotonic.markNow()
        this.interval = interval
    }

    fun setCooldown(interval: String) = setCooldown(Duration.parse(interval))

    /**
     * 立刻结束冷却
     */
    @Synchronized
    fun stopImmediately() {
        // 立刻结束冷却，重置开始标记和持续时间
        interval = Duration.ZERO
        startMark = TimeSource.Monotonic.markNow()
    }

    /**
     * 增加冷却时长
     */
    @Synchronized
    fun increase(interval: Duration) {
        this.interval = this.interval + interval
    }

    fun increase(interval: String) = increase(Duration.parse(interval))

    /**
     * 减少冷却时长
     */
    @Synchronized
    fun decrease(interval: Duration) {
        this.interval = this.interval - interval
    }

    fun decrease(interval: String) = decrease(Duration.parse(interval))

    /**
     * 为玩家设置某个材质的冷却动画
     */
    fun `for`(material: ItemMaterial) {
        // 1.11.2 之前没有冷却动画
        if (MinecraftVersion.versionId < 11102) return
        val player = Bukkit.getPlayer(uuid.toJavaUuid()) ?: return
        // 剩余冷却时间 (ticks)
        val left = remaining.inWholeMilliseconds / 50 // 1 tick = 50 ms
        // 小薯片说 1.11.2 才有这个
        // void setCooldown(@NotNull Material var1, int var2)
        player.setCooldown(material.toBukkit(), left.toInt())
    }

    /**
     * 为玩家设置某个材质的冷却动画
     */
    fun `for`(item: NeoItem) {
        `for`(item.data.material)
    }

}
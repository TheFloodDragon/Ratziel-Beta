@file:OptIn(ExperimentalUuidApi::class)

package cn.fd.ratziel.module.item.feature.cooldown

import cn.fd.ratziel.core.Identifier
import org.bukkit.entity.Player
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toKotlinUuid

/**
 * Cooldown
 *
 * @author TheFloodDragon
 * @since 2025/5/31 13:51
 */
class Cooldown(
    /**
     * 冷却标识符 (物品标识符)
     */
    val identifier: Identifier,
) {

    /**
     * 冷却单元组表
     *
     * 玩家 [Uuid] -> 冷却单元组 (冷却名称 -> 冷却单元 [CooldownUnit])
     */
    private val unitGroups: MutableMap<Uuid, MutableMap<String, CooldownUnit>> = HashMap()

    /**
     * 获取冷却单元
     */
    @Synchronized
    operator fun get(uuid: Uuid, name: String): CooldownUnit {
        val group = unitGroups.computeIfAbsent(uuid) { HashMap() }
        return group.computeIfAbsent(name) {
            CooldownUnit(uuid, name)
        }
    }

    /**
     * 获取冷却单元组
     */
    @Synchronized
    fun getGroup(uuid: Uuid): Collection<CooldownUnit> {
        return unitGroups.computeIfAbsent(uuid) { HashMap() }.values
    }

    /**
     * 获取冷却单元
     */
    operator fun get(player: Player, name: String): CooldownUnit {
        return get(player.uniqueId.toKotlinUuid(), name)
    }

    /**
     * 获取冷却单元组
     */
    fun getGroup(player: Player): Collection<CooldownUnit> {
        return getGroup(player.uniqueId.toKotlinUuid())
    }

}
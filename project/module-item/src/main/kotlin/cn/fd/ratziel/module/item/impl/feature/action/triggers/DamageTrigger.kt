package cn.fd.ratziel.module.item.impl.feature.action.triggers

import cn.fd.ratziel.module.item.api.feature.ItemTrigger
import cn.fd.ratziel.module.item.impl.feature.action.ActionManager
import org.bukkit.event.player.PlayerItemDamageEvent
import taboolib.common.platform.event.SubscribeEvent

/**
 * DamageTrigger
 *
 * @author TheFloodDragon
 * @since 2024/7/8 15:43
 */
internal object DamageTrigger : ItemTrigger {

    override val names = arrayOf("onDamage", "damage")

    @SubscribeEvent
    fun onDamage(event: PlayerItemDamageEvent) {
        // 获取攻击时的物品
        val item = event.item
        val neoItem = AttackTrigger.asNeo(item) ?: return
        // 触发触发器
        ActionManager.trigger(neoItem.identifier, this) {
            set("event", event)
            set("player", event.player)
            set("damage", event.damage)
            set("item", event.item)
            set("neoItem", neoItem)
        }
    }

}
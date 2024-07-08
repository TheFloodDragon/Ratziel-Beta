package cn.fd.ratziel.module.item.impl.feature.action.triggers

import cn.fd.ratziel.module.item.api.feature.ItemTrigger
import cn.fd.ratziel.module.item.impl.feature.action.ActionManager
import org.bukkit.event.player.PlayerItemBreakEvent
import taboolib.common.platform.event.SubscribeEvent

/**
 * BreakTrigger
 *
 * @author TheFloodDragon
 * @since 2024/7/8 16:19
 */
object BreakTrigger : ItemTrigger {

    override val names = arrayOf("onBreak", "break")

    @SubscribeEvent(ignoreCancelled = true)
    fun onBreak(event: PlayerItemBreakEvent) {
        // 获取损坏时的物品
        val item = event.brokenItem
        val neoItem = AttackTrigger.asNeo(item) ?: return
        // 触发触发器
        ActionManager.trigger(neoItem.identifier, this) {
            set("event", event)
            set("player", event.player)
            set("item", item)
            set("neoItem", neoItem)
        }
    }

}
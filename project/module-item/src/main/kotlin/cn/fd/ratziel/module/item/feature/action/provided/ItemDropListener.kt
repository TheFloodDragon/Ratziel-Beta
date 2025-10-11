package cn.fd.ratziel.module.item.feature.action.provided

import cn.fd.ratziel.module.item.feature.action.ActionManager.registerSimple
import cn.fd.ratziel.module.item.feature.action.ActionManager.trigger
import cn.fd.ratziel.module.item.impl.RatzielItem
import cn.fd.ratziel.module.item.util.toItemStack
import org.bukkit.event.player.PlayerDropItemEvent
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent

/**
 * ItemDropListener
 *
 * @author TheFloodDragon
 * @since 2025/6/8 10:07
 */
@Awake
object ItemDropListener {

    /** 当物品被丢弃时 **/
    val ON_DROP = registerSimple("onDrop", "drop")

    @SubscribeEvent
    fun onItemDropped(event: PlayerDropItemEvent) {
        // 掉落物 (实体)
        val dropped = event.itemDrop
        // 特征物品处理
        val ratzielItem = RatzielItem.sourced(dropped.itemStack) ?: return

        // 触发触发器
        ON_DROP.trigger(ratzielItem.identifier, event.player, ratzielItem) {
            set("event", event) // 事件
            set("dropped", dropped) // 掉落物 (实体)
            set("player", event.player) // 丢弃物品的玩家
            set("item", ratzielItem) // 丢弃的物品 (RatzielItem)
        }
        // 将修改后的物品重新写到玩家手上
        if (ratzielItem.overwrite) dropped.itemStack = ratzielItem.toItemStack()
    }

}
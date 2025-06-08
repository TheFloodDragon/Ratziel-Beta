package cn.fd.ratziel.module.item.impl.action.provided

import cn.fd.ratziel.module.item.impl.RatzielItem
import cn.fd.ratziel.module.item.impl.action.ActionManager.trigger
import cn.fd.ratziel.module.item.impl.action.registerTrigger
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
    val ON_DROP = registerTrigger("onDrop", "drop")

    @SubscribeEvent
    fun onItemDropped(event: PlayerDropItemEvent) {
        // 掉落物 (实体)
        val dropped = event.itemDrop
        // 特征物品处理
        val ratzielItem = RatzielItem.of(dropped.itemStack) ?: return

        // 触发触发器
        ON_DROP.trigger(ratzielItem.identifier, event.player, ratzielItem) {
            set("event", event) // 事件
            set("dropped", dropped) // 掉落物 (实体)
            set("player", event.player) // 丢弃物品的玩家
            set("item", ratzielItem) // 丢弃的物品 (RatzielItem)
        }
        // 将修改后的物品重新写到玩家手上
        dropped.itemStack = ratzielItem.toItemStack()
    }

}
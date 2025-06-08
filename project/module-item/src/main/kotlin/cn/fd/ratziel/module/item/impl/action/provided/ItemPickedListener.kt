package cn.fd.ratziel.module.item.impl.action.provided

import cn.fd.ratziel.module.item.impl.RatzielItem
import cn.fd.ratziel.module.item.impl.action.ActionManager.trigger
import cn.fd.ratziel.module.item.impl.action.registerTrigger
import cn.fd.ratziel.module.item.util.toItemStack
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityPickupItemEvent
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent

/**
 * ItemPickedListener
 *
 * @author TheFloodDragon
 * @since 2025/6/8 11:50
 */
@Awake
object ItemPickedListener {

    /** 当物品被拾取时 **/
    val ON_PICK = registerTrigger("onPickedUp", "onPick", "pick")

    @SubscribeEvent // EntityPickupItemEvent 好像在 1.8 的时候没有
    fun onItemPicked(event: EntityPickupItemEvent) {
        // 掉落物 (实体)
        val picked = event.item
        // 特征物品处理
        val ratzielItem = RatzielItem.of(picked.itemStack) ?: return

        // 触发触发器
        val player = event.entity as? Player
        ON_PICK.trigger(ratzielItem.identifier, player, ratzielItem) {
            set("event", event) // 事件
            set("entity", event.entity) // 捡起物品的实体
            set("player", player) // 玩家 (不是玩家时为空)
            set("picked", picked) // 掉落物 (实体)
            set("item", ratzielItem) // 捡起的物品 (RatzielItem)
        }
        // 将修改后的物品重新写到玩家手上
        picked.itemStack = ratzielItem.toItemStack()
    }

}
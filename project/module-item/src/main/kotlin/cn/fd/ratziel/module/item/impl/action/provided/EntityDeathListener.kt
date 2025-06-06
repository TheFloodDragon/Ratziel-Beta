package cn.fd.ratziel.module.item.impl.action.provided

import cn.fd.ratziel.module.item.impl.RatzielItem
import cn.fd.ratziel.module.item.impl.action.ActionManager.trigger
import cn.fd.ratziel.module.item.impl.action.registerTrigger
import cn.fd.ratziel.module.item.util.writeTo
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDeathEvent
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.platform.util.killer

/**
 * EntityDeathListener
 *
 * @author TheFloodDragon
 * @since 2025/5/31 13:05
 */
@Awake
object EntityDeathListener {

    /* 击杀生物时 */
    val KILL_ENTITY = registerTrigger("onKill", "kill")

    @SubscribeEvent
    fun onEntityDeath(event: EntityDeathEvent) {
        // 玩家杀手
        val killer = event.killer as? Player ?: return
        // 判断手持物品
        val itemInHand = killer.inventory.itemInMainHand
        // 特征物品处理
        val ratzielItem = RatzielItem.of(itemInHand) ?: return

        // 触发触发器
        KILL_ENTITY.trigger(ratzielItem.identifier, killer, ratzielItem) {
            set("event", event) // 事件
            set("player", killer) // 玩家 & 杀手
            set("killer", killer) // 杀手
            set("item", ratzielItem) // 使用的物品 (凶器)
            set("entity", event.entity) // 被击杀的实体
        }
        // 将修改后的物品重新写到玩家手上
        ratzielItem.writeTo(itemInHand)
    }

}
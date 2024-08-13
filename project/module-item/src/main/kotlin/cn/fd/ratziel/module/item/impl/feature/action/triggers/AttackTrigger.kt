package cn.fd.ratziel.module.item.impl.feature.action.triggers

import cn.fd.ratziel.module.item.api.feature.ItemTrigger
import cn.fd.ratziel.module.item.impl.RatzielItem
import cn.fd.ratziel.module.item.impl.feature.action.ActionManager
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.event.SubscribeEvent
import taboolib.platform.util.attacker
import taboolib.platform.util.isAir

/**
 * AttackTrigger
 *
 * @author TheFloodDragon
 * @since 2024/7/3 19:06
 */
internal object AttackTrigger : ItemTrigger {

    override val names = arrayOf("onAttack", "onAtk", "attack", "atk")

    @SubscribeEvent
    fun onAttack(event: EntityDamageByEntityEvent) {
        // 获取攻击者
        val attacker = event.attacker
        // 判断是否是玩家, 物品是否是本插件物品
        if (attacker !is Player) return
        // 获取攻击时的物品
        val item = attacker.inventory.itemInMainHand
        val neoItem = asNeo(item) ?: return
        // 触发触发器 (参数: 事件(EntityDamageByEntityEvent), 攻击者(Player), ItemStack, RatzielItem)
        ActionManager.trigger(neoItem.id, this) {
            set("event", event)
            set("attacker", attacker)
            set("item", item)
            set("neoItem", neoItem)
        }
    }

    fun asNeo(item: ItemStack): RatzielItem? {
        if (item.isAir()) return null
        return RatzielItem.of(item)
    }

}
@file:Suppress("unused")

package cn.fd.ratziel.module.item.impl.action

import cn.fd.ratziel.module.item.api.event.ItemGenerateEvent
import cn.fd.ratziel.module.item.impl.RatzielItem
import cn.fd.ratziel.script.impl.SimpleScriptEnv
import org.bukkit.entity.Player
import org.bukkit.event.block.Action.*
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemBreakEvent
import org.bukkit.event.player.PlayerItemDamageEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.platform.util.attacker

/**
 * TriggerListener
 *
 * @author TheFloodDragon
 * @since 2024/8/13 14:35
 */
object TriggerListener {

    @SubscribeEvent
    fun onRelease(event: ItemGenerateEvent.Post) {
        Triggers.RELEASE.trigger(event.identifier) {
            set("event", event)
            set("neoItem", event.item)
            set("context", event.context)
        }
    }

    @SubscribeEvent
    fun onAttack(event: EntityDamageByEntityEvent) {
        // 获取攻击者
        val attacker = event.attacker
        // 判断是否是玩家, 物品是否是本插件物品
        if (attacker !is Player) return
        // 获取攻击时的物品
        val item = attacker.inventory.itemInMainHand
        val neoItem = RatzielItem.of(item) ?: return
        // 触发触发器 (参数: 事件(EntityDamageByEntityEvent), 攻击者(Player), ItemStack, RatzielItem)
        Triggers.ATTACK.trigger(neoItem.id) {
            set("event", event)
            set("attacker", attacker)
            set("item", item)
            set("neoItem", neoItem)
        }
    }

    @SubscribeEvent(ignoreCancelled = true)
    fun onBreak(event: PlayerItemBreakEvent) {
        // 获取损坏时的物品
        val item = event.brokenItem
        val neoItem = RatzielItem.of(item) ?: return
        // 触发触发器
        Triggers.BREAK.trigger(neoItem.id) {
            set("event", event)
            set("player", event.player)
            set("item", item)
            set("neoItem", neoItem)
        }
    }

    @SubscribeEvent
    fun onDamage(event: PlayerItemDamageEvent) {
        // 获取攻击时的物品
        val item = event.item
        val neoItem = RatzielItem.of(item) ?: return
        // 触发触发器
        Triggers.DAMAGED.trigger(neoItem.id) {
            set("event", event)
            set("player", event.player)
            set("damage", event.damage)
            set("item", event.item)
            set("neoItem", neoItem)
        }
    }

    /**
     * 当玩家与空气或方块发生交互时
     * 触发事件及脚本
     */
    @SubscribeEvent
    fun onInteract(event: PlayerInteractEvent) {
        // 获取交互时的物品
        val item = event.item ?: return
        val neoItem = RatzielItem.of(item) ?: return
        // 环境
        val env = SimpleScriptEnv().apply {
            set("event", event)
            set("item", item)
            set("neoItem", neoItem)
        }
        // 触发触发器
        when (event.action) {
            LEFT_CLICK_AIR -> Triggers.INTERACT_LEFT_CLICK_AIR.trigger(neoItem.id, env)
            LEFT_CLICK_BLOCK -> Triggers.INTERACT_LEFT_CLICK_BLOCK.trigger(neoItem.id, env)
            RIGHT_CLICK_AIR -> Triggers.INTERACT_RIGHT_CLICK_AIR.trigger(neoItem.id, env)
            RIGHT_CLICK_BLOCK -> Triggers.INTERACT_RIGHT_CLICK_BLOCK.trigger(neoItem.id, env)
            else -> {}
        }
    }

}
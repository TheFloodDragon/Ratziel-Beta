package cn.fd.ratziel.module.item.impl.action.provided

import cn.fd.ratziel.core.function.SimpleContext
import cn.fd.ratziel.module.item.RatzielItem
import cn.fd.ratziel.module.item.api.action.ItemTrigger
import cn.fd.ratziel.module.item.impl.action.ActionManager.trigger
import cn.fd.ratziel.module.item.impl.action.registerTrigger
import cn.fd.ratziel.module.script.impl.SimpleScriptEnv
import taboolib.common.platform.event.SubscribeEvent
import taboolib.platform.event.PlayerWorldContactEvent

/**
 * WorldContactListener
 *
 * @author TheFloodDragon
 * @since 2025/5/2 12:47
 */
object WorldContactListener {

    /* 左右键交互 */
    val INTERACT_LEFT_CLICK = registerTrigger("onLeft", "left", "onLeftClick", "leftClick")
    val INTERACT_RIGHT_CLICK = registerTrigger("onLeft", "left", "onLeftClick", "leftClick")

    /* 左右键交互具体事物 */
    val INTERACT_LEFT_CLICK_AIR = registerTrigger("onLeftAir", "left-air")
    val INTERACT_RIGHT_CLICK_AIR = registerTrigger("onRightAir", "right-air")
    val INTERACT_LEFT_CLICK_BLOCK = registerTrigger("onLeftBlock", "left-block")
    val INTERACT_RIGHT_CLICK_BLOCK = registerTrigger("onRightBlock", "right-block")

    /* 同样也是攻击触发器 (EntityDamageByEntityEvent) */
    val INTERACT_LEFT_CLICK_ENTITY = registerTrigger("onLeftEntity", "left-entity", "onAttack", "onAtk", "attack", "atk")

    /* PlayerInteractAtEntityEvent */
    val INTERACT_RIGHT_CLICK_ENTITY = registerTrigger("onRightEntity", "right-entity")

    @SubscribeEvent
    fun onWorldContact(event: PlayerWorldContactEvent) {
        // 交互用的物品
        val itemStack = event.player.inventory.getItem(event.action.hand)
        // 仅处理 RatzielItem 物品, 其它的直接返回
        val ratzielItem = RatzielItem.of(itemStack ?: return) ?: return

        // 初始化环境
        val environment = SimpleScriptEnv().apply {
            set("event", event)
            set("player", event.player)
            set("itemStack", itemStack)
            set("neoItem", ratzielItem)
        }

        fun trigger(
            trigger: ItemTrigger,
            action: SimpleScriptEnv.() -> Unit = {},
        ) {
            action(environment)
            trigger.trigger(ratzielItem.id, SimpleContext(environment))
        }

        // 分配处理器
        when {
            event.isLeftClick -> trigger(INTERACT_LEFT_CLICK)
            event.isRightClick -> trigger(INTERACT_RIGHT_CLICK)
            event.isLeftClickAir -> trigger(INTERACT_LEFT_CLICK_AIR)
            event.isRightClickAir -> trigger(INTERACT_RIGHT_CLICK_AIR)

            // Block
            event.isLeftClickBlock -> trigger(INTERACT_LEFT_CLICK_BLOCK) {
                val action = event.action as PlayerWorldContactEvent.Action.LeftClickBlock
                set("block", action.block)
            }

            event.isRightClickBlock -> trigger(INTERACT_RIGHT_CLICK_BLOCK) {
                val action = event.action as PlayerWorldContactEvent.Action.LeftClickBlock
                set("block", action.block)
            }

            // Entity
            event.isLeftClickEntity -> trigger(INTERACT_LEFT_CLICK_ENTITY) {
                val action = event.action as PlayerWorldContactEvent.Action.LeftClickEntity
                set("target", action.entity)
                set("attacker", event.player)
            }

            event.isRightClickEntity -> trigger(INTERACT_RIGHT_CLICK_ENTITY) {
                val action = event.action as PlayerWorldContactEvent.Action.RightClickEntity
                set("taget", action.entity)
            }
        }
    }

}
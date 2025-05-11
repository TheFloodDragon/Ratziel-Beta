package cn.fd.ratziel.module.item.impl.action.provided

import cn.fd.ratziel.core.function.SimpleContext
import cn.fd.ratziel.module.item.api.action.ItemTrigger
import cn.fd.ratziel.module.item.impl.RatzielItem
import cn.fd.ratziel.module.item.impl.action.ActionManager.trigger
import cn.fd.ratziel.module.item.impl.action.registerTrigger
import cn.fd.ratziel.module.item.internal.nms.RefItemStack
import cn.fd.ratziel.module.script.impl.SimpleScriptEnv
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.platform.event.PlayerWorldContactEvent

/**
 * WorldContactListener
 *
 * @author TheFloodDragon
 * @since 2025/5/2 12:47
 */
@Awake
object WorldContactListener {

    /* 任意交互 */
    val INTERACT_ANY = registerTrigger("onInteract", "Interact", "interact")

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
            set("item", ratzielItem)
            /*
               此处不开放事件的 ItemStack,
               是因为 ItemStack 最后都会被 RatzielItem 重新写入数据,
               故而就算脚本运行过程中修改了 ItemStack,
               最后所看的也只会是 RatzielItem 转化成的 ItemStack 罢了
            */
        }

        fun trigger(
            trigger: ItemTrigger,
            action: SimpleScriptEnv.() -> Unit = {},
        ) {
            action(environment)
            trigger.trigger(ratzielItem.identifier, SimpleContext(environment))
            // 向事件的 ItemStack 写入 RatzielItem 的数据
            RefItemStack.of(ratzielItem.data).writeTo(itemStack)
        }

        // 任意交互
        trigger(INTERACT_ANY)
        // 分配处理器
        when {
            event.isLeftClickAir -> trigger(INTERACT_LEFT_CLICK_AIR)
            event.isRightClickAir -> trigger(INTERACT_RIGHT_CLICK_AIR)

            // Block
            event.isLeftClickBlock -> trigger(INTERACT_LEFT_CLICK_BLOCK) {
                val action = event.action as PlayerWorldContactEvent.Action.LeftClickBlock
                set("block", action.block)
            }

            event.isRightClickBlock -> trigger(INTERACT_RIGHT_CLICK_BLOCK) {
                val action = event.action as PlayerWorldContactEvent.Action.RightClickBlock
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
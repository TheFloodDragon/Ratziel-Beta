package cn.fd.ratziel.module.item.impl.feature.action.triggers

import cn.fd.ratziel.core.Identifier
import cn.fd.ratziel.module.item.api.feature.ItemTrigger
import cn.fd.ratziel.module.item.impl.feature.action.ActionManager
import cn.fd.ratziel.script.api.ScriptEnvironment
import cn.fd.ratziel.script.impl.SimpleScriptEnv
import org.bukkit.event.block.Action.*
import org.bukkit.event.player.PlayerInteractEvent
import taboolib.common.platform.event.SubscribeEvent

/**
 * InteractTrigger
 *
 * @author TheFloodDragon
 * @since 2024/7/8 16:09
 */
object InteractTrigger {

    /**
     * 当玩家与空气或方块发生交互时
     * 触发事件及脚本
     */
    @SubscribeEvent
    fun onInteract(event: PlayerInteractEvent) {
        // 获取交互时的物品
        val item = event.item ?: return
        val neoItem = AttackTrigger.asNeo(item) ?: return
        // 环境
        val env = SimpleScriptEnv().apply {
            set("event", event)
            set("item", item)
            set("neoItem", neoItem)
        }
        // 触发触发器
        when (event.action) {
            LEFT_CLICK_AIR -> Triggers.LEFT_CLICK_AIR.trigger(neoItem.identifier, env)
            LEFT_CLICK_BLOCK -> Triggers.LEFT_CLICK_BLOCK.trigger(neoItem.identifier, env)
            RIGHT_CLICK_AIR -> Triggers.RIGHT_CLICK_AIR.trigger(neoItem.identifier, env)
            RIGHT_CLICK_BLOCK -> Triggers.RIGHT_CLICK_BLOCK.trigger(neoItem.identifier, env)
            else -> {}
        }
    }

    enum class Triggers(
        override vararg val names: String,
        val parent: ItemTrigger? = null
    ) : ItemTrigger {

        /**
         * includes [LEFT_CLICK_AIR] [LEFT_CLICK_BLOCK]
         */
        LEFT_CLICK("onLeft", "left", "onLeftClick", "leftClick"),

        /**
         * includes [RIGHT_CLICK_AIR] [RIGHT_CLICK_BLOCK]
         */
        RIGHT_CLICK("onLeft", "left", "onLeftClick", "leftClick"),

        LEFT_CLICK_AIR("onLeftClickedAir", "left-air", "left-click-air", parent = LEFT_CLICK),
        LEFT_CLICK_BLOCK("onLeftClickedBlock", "left-block", "left-click-block", parent = LEFT_CLICK),
        RIGHT_CLICK_AIR("onRightClickedAir", "right-air", "right-click-air", parent = RIGHT_CLICK),
        RIGHT_CLICK_BLOCK("onRightClickedBlock", "right-block", "right-click-block", parent = RIGHT_CLICK);

        fun trigger(identifier: Identifier, environment: ScriptEnvironment) {
            // Trigger myself
            ActionManager.trigger(identifier, this, environment)
            // Trigger parent
            if (parent != null) ActionManager.trigger(identifier, parent, environment)
        }

    }

}
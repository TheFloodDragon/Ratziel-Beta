package cn.fd.ratziel.module.item.impl.feature.action.triggers

import cn.fd.ratziel.module.item.api.feature.ItemTrigger
import cn.fd.ratziel.module.item.impl.feature.action.ActionManager
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
            set("block", event.clickedBlock)
            set("position", event.clickedPosition)
        }
        // 触发触发器
        when (event.action) {

            // 左键
            LEFT_CLICK_AIR, LEFT_CLICK_BLOCK ->
                ActionManager.trigger(neoItem.identifier, LeftClick, env)

            // 右键
            RIGHT_CLICK_AIR, RIGHT_CLICK_BLOCK ->
                ActionManager.trigger(neoItem.identifier, RightClick, env)

            else -> {}
        }
    }

    object LeftClick : ItemTrigger {
        override val names = arrayOf("onLeft", "left", "onLeftClick", "leftClick")
    }

    object RightClick : ItemTrigger {
        override val names = arrayOf("onRight", "right", "onRightClick", "rightClick")
    }

}
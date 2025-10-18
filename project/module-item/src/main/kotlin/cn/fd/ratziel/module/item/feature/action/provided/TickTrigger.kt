package cn.fd.ratziel.module.item.feature.action.provided

import cn.fd.ratziel.common.block.ExecutableBlock
import cn.fd.ratziel.common.event.ElementEvaluateEvent
import cn.fd.ratziel.core.Identifier
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.util.getBy
import cn.fd.ratziel.module.item.ItemElement
import cn.fd.ratziel.module.item.feature.action.ActionManager
import cn.fd.ratziel.module.item.feature.action.ActionManager.trigger
import cn.fd.ratziel.module.item.feature.action.ItemTrigger
import cn.fd.ratziel.module.item.impl.RatzielItem
import cn.fd.ratziel.module.item.internal.command.PlayerInventorySlot
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.intOrNull
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import taboolib.common.platform.service.PlatformExecutor
import taboolib.platform.util.onlinePlayers
import java.util.concurrent.CopyOnWriteArraySet

/**
 * TickTrigger
 *
 * @author TheFloodDragon
 * @since 2025/6/8 10:15
 */
object TickTrigger : ItemTrigger("onTick", "tick") {

    private val tasks = HashMap<Int, PlatformExecutor.PlatformTask>()

    private val targets = HashMap<Int, MutableCollection<Identifier>>()

    override fun build(identifier: Identifier, element: Element): ExecutableBlock {
        val property = element.property
        if (property !is JsonObject) {
            return super.build(identifier, element)
        }
        // 运行内容
        val code = property.getBy("run", "code") ?: throw IllegalArgumentException("Code block in onTick Trigger must not be null!")
        // 周期 (tick)
        val period = (property["period"] as? JsonPrimitive)?.intOrNull ?: 1
        // 栏位
        val slot = (property.getBy("where", "slot") as? JsonPrimitive)
            ?.let { PlayerInventorySlot.inferOrAny(it.content) }
            ?: PlayerInventorySlot.MAIN_HAND

        // 加入到要被 tick 的目标中去
        targets.computeIfAbsent(period) { CopyOnWriteArraySet() }
            .add(identifier)

        // 初始化此间隔的任务 (tasks里没有时才提交, 避免每生成一个物品注册了一个Timer的情况)
        tasks.computeIfAbsent(period) {
            submit(period = period.toLong()) {
                // 获取要 tick 的物品类型
                val identifiers = targets[period]
                if (identifiers != null && identifiers.isNotEmpty()) {
                    // 全部 tick 一遍
                    for (player in onlinePlayers) tick(player, identifiers, slot)
                }
            }
        }
        // 构建脚本块并返回
        return super.build(identifier, element.copyOf(code))
    }

    private fun tick(player: Player, identifiers: Iterable<Identifier>, slot: Any) {
        if (slot is PlayerInventorySlot) {
            // 指定栏位物品 tick
            tick(player, slot.getItemFrom(player) ?: return, identifiers)
        } else if (slot is PlayerInventorySlot.AnySlotMark) {
            // 任意栏位物品 tick
            for (item in player.inventory.contents) {
                tick(player, item, identifiers)
            }
        }
    }

    private fun tick(player: Player, itemStack: ItemStack, identifiers: Iterable<Identifier>) {
        // 特征物品处理
        val ratzielItem = RatzielItem.sourced(itemStack, false) ?: return
        // 判断是不是要用的物品
        if (ratzielItem.identifier in identifiers) return

        // 触发动作
        TickTrigger.trigger(ratzielItem.identifier, player, ratzielItem) {
            set("player", player)
            set("item", ratzielItem)
        }
        // 重新写回物品
        ratzielItem.overwrite()
    }

    @Suppress("unused")
    @SubscribeEvent
    private fun onUpdate(event: ElementEvaluateEvent.Start) {
        if (event.handler !is ItemElement) return
        // 清空要 tick 的目标
        targets.clear()
    }

    @Awake
    private fun registerMySelf() {
        ActionManager.register(this)
    }

}
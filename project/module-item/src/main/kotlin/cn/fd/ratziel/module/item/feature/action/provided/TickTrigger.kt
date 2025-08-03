package cn.fd.ratziel.module.item.feature.action.provided

import cn.fd.ratziel.common.event.ElementEvaluateEvent
import cn.fd.ratziel.core.Identifier
import cn.fd.ratziel.core.serialization.json.getBy
import cn.fd.ratziel.module.item.impl.RatzielItem
import cn.fd.ratziel.module.item.feature.action.ActionManager.trigger
import cn.fd.ratziel.module.item.feature.action.SimpleTrigger
import cn.fd.ratziel.module.item.internal.command.PlayerInventorySlot
import cn.fd.ratziel.module.script.block.ExecutableBlock
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.intOrNull
import org.bukkit.entity.Player
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import taboolib.common.platform.service.PlatformExecutor
import taboolib.platform.util.onlinePlayers
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.iterator

/**
 * TickTrigger
 *
 * @author TheFloodDragon
 * @since 2025/6/8 10:15
 */
object TickTrigger : SimpleTrigger("onTick", "tick") {

    private val tasks = ConcurrentHashMap<Identifier, PlatformExecutor.PlatformTask>()

    override fun build(identifier: Identifier, element: JsonElement): ExecutableBlock {
        if (element is JsonObject) {
            // 周期 (tick)
            val period = (element["period"] as? JsonPrimitive)?.intOrNull ?: 1
            // 栏位
            val slot = (element.getBy("where", "slot") as? JsonPrimitive)
                ?.let { PlayerInventorySlot.infer(it.content) }
                ?: PlayerInventorySlot.MAIN_HAND
            // 提交任务 (缓存里没有时才提交, 避免每生成一个物品注册了一个Timer的情况)
            tasks.computeIfAbsent(identifier) {
                submit(period = period.toLong()) {
                    // 全部 tick 一遍
                    for (player in onlinePlayers) tick(player, identifier, slot)
                }
            }
            // 构建脚本块并返回
            return super.build(identifier, element.getBy("run", "code") ?: throw IllegalArgumentException("Code in onTrigger must not be null!"))
        }
        return super.build(identifier, element)
    }

    private fun tick(player: Player, identifier: Identifier, slot: PlayerInventorySlot) {
        // 获取指定栏位物品
        val itemStack = slot.getItemFrom(player) ?: return
        // 特征物品处理
        val ratzielItem = RatzielItem.of(itemStack) ?: return
        // 判断是不是要用的物品
        if (ratzielItem.identifier != identifier) return

        // 触发动作
        TickTrigger.trigger(identifier, player, ratzielItem) {
            set("player", player)
            set("item", ratzielItem)
        }
    }

    @Suppress("unused")
    @SubscribeEvent
    private fun onUpdate(event: ElementEvaluateEvent.Start) {
        for (task in tasks) {
            // 更新时必须取消上一个任务
            task.value.cancel()
        }
        tasks.clear()
    }

}
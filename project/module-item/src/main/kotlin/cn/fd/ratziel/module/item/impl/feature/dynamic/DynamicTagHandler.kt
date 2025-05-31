package cn.fd.ratziel.module.item.impl.feature.dynamic

import cn.fd.ratziel.common.message.Message
import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.core.function.SimpleContext
import cn.fd.ratziel.core.util.splitNonEscaped
import cn.fd.ratziel.module.item.api.builder.ItemTagResolver
import cn.fd.ratziel.module.item.impl.RatzielItem
import cn.fd.ratziel.module.item.impl.component.ItemDisplay
import cn.fd.ratziel.module.item.internal.nms.RefItemStack
import net.kyori.adventure.text.TextReplacementConfig
import org.bukkit.GameMode
import taboolib.common.platform.event.SubscribeEvent
import taboolib.module.nms.PacketSendEvent
import kotlin.jvm.optionals.getOrElse

/**
 * DynamicTagHandler
 *
 * @author TheFloodDragon
 * @since 2025/5/31 17:27
 */
object DynamicTagHandler {

    @SubscribeEvent
    private fun onSend(event: PacketSendEvent) {
        if (event.packet.name != "ClientboundContainerSetSlotPacket"
            && event.packet.name != "PacketPlayOutSetSlot"
        ) return

        // 玩家模式仅处理 生存 和 冒险 模式, 原因: 创造强制设置库存, 旁观没什么必要
        if (event.player.gameMode != GameMode.SURVIVAL
            && event.player.gameMode != GameMode.ADVENTURE
        ) return

        // 获取物品 TODO 完善版本支持
        val nmsItem = event.packet.read<Any>("itemStack") ?: return
        val refItem = RefItemStack.ofNms(nmsItem)
        // 生成 RatzielItem 实例 (如果不是就跳过处理)
        val ratzielItem = RatzielItem.of(refItem.extractData()) ?: return

        // 生成上下文
        val context = SimpleContext(event.player, ratzielItem)

        // 处理显示组件
        handleDisplay(ratzielItem, context)

        // 最后写回物品数据
        RefItemStack.of(ratzielItem.data).writeTo(refItem.bukkitStack)
    }

    fun handleDisplay(ratzielItem: RatzielItem, context: ArgumentContext) {
        val display = ratzielItem.getComponent(ItemDisplay::class.java)

        // 显示名称处理
        val newName = display.name?.replaceText(createReplacementConfig(context))
        // 本地化名称处理
        val newLocalName = display.localizedName?.replaceText(createReplacementConfig(context))
        // Lore 处理
        val newLore = display.lore?.map { it.replaceText(createReplacementConfig(context)) }

        // 创建新的显示组件
        val newDisplay = ItemDisplay(newName, newLocalName, newLore)

        // 写入物品
        ratzielItem.setComponent(newDisplay)
    }

    fun createReplacementConfig(context: ArgumentContext) = TextReplacementConfig.builder().apply {
        match(DynamicTagResolver.regex)
        replacement { text ->
            val content = text.content()
                .drop(DynamicTagResolver.IDENTIFIED_START.length)
                .dropLast(DynamicTagResolver.IDENTIFIED_END.length)
            val split = content.splitNonEscaped(DynamicTagResolver.IDENTIFIED_SEPARATION)
            val name = split.firstOrNull() ?: return@replacement text
            val resolver = DynamicTagService.findResolver(name) ?: return@replacement text
            val assignment = ItemTagResolver.Assignment(split.drop(1), null)
            resolver.resolve(assignment, context)
            Message.buildMessage(assignment.result.getOrElse { return@replacement text })
        }
    }.build()

}
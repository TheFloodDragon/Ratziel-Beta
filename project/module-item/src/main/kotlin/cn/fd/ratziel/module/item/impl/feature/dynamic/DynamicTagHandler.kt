package cn.fd.ratziel.module.item.impl.feature.dynamic

import cn.fd.ratziel.common.message.Message
import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.core.function.SimpleContext
import cn.fd.ratziel.module.item.impl.RatzielItem
import cn.fd.ratziel.module.item.impl.component.ItemDisplay
import cn.fd.ratziel.module.item.internal.nms.RefItemStack
import net.kyori.adventure.text.TextReplacementConfig
import taboolib.common.platform.event.SubscribeEvent
import taboolib.module.nms.PacketSendEvent

/**
 * DynamicTagHandler
 *
 * @author TheFloodDragon
 * @since 2025/5/31 17:27
 */
object DynamicTagHandler {

    @SubscribeEvent
    private fun onSend(event: PacketSendEvent) {
        if (event.packet.name != "PacketPlayOutSetSlot") return
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
        for ((literal, resolvation) in DynamicTagService.resolvations) {
            matchLiteral(literal)
            val component = Message.buildMessage(resolvation.resolve(context))
            replacement(component)
        }
    }.build()

}
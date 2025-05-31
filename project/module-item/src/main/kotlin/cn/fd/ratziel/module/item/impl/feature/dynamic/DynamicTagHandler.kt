package cn.fd.ratziel.module.item.impl.feature.dynamic

import cn.fd.ratziel.common.message.Message
import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.core.function.SimpleContext
import cn.fd.ratziel.module.item.impl.RatzielItem
import cn.fd.ratziel.module.item.impl.component.ItemDisplay
import cn.fd.ratziel.module.item.internal.nms.RefItemStack
import cn.fd.ratziel.module.item.util.writeTo
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextReplacementConfig
import org.bukkit.GameMode
import org.bukkit.event.player.PlayerGameModeChangeEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import taboolib.module.nms.PacketSendEvent

/**
 * DynamicTagHandler
 *
 * @author TheFloodDragon
 * @since 2025/5/31 17:27
 */
@Suppress("unused")
object DynamicTagHandler {

    @SubscribeEvent
    private fun onGameModeChange(event: PlayerGameModeChangeEvent) {
        val player = event.player
        if (player.gameMode == GameMode.CREATIVE || event.newGameMode == GameMode.CREATIVE) {
            submit {
                @Suppress("UnstableApiUsage")
                player.updateInventory()
            }
        }
    }

    @SubscribeEvent
    private fun onSend(event: PacketSendEvent) {
        if (event.packet.name == "ClientboundContainerSetSlotPacket"
            || event.packet.name == "PacketPlayOutSetSlot"
        ) {

            // 玩家模式仅处理 生存 和 冒险 模式, 原因: 创造强制设置库存, 旁观没什么必要
            if (event.player.gameMode == GameMode.CREATIVE
                || event.player.gameMode == GameMode.SPECTATOR
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
            ratzielItem.writeTo(refItem.bukkitStack)
        }
    }

    fun handleDisplay(ratzielItem: RatzielItem, context: ArgumentContext) = runBlocking {
        val display = ratzielItem.getComponent(ItemDisplay::class.java)

        fun Component.replace(): Deferred<Component> {
            return async { this@replace.replaceText(createReplacementConfig(context)) }
        }

        // 显示名称处理
        val newName = display.name?.replace()
        // 本地化名称处理
        val newLocalName = display.localizedName?.replace()
        // Lore 处理
        val newLore = display.lore?.map { it.replace() }

        // 创建新显示组件
        val newDisplay = ItemDisplay(
            newName?.await(),
            newLocalName?.await(),
            newLore?.awaitAll()
        )

        // 写入物品
        ratzielItem.setComponent(newDisplay)
    }

    fun createReplacementConfig(context: ArgumentContext) = TextReplacementConfig.builder().apply {
        match(DynamicTagResolver.regex)
        replacement { text ->
            val resolved = DynamicTagResolver.resolveTag(text.content(), context)
                ?: return@replacement text
            Message.buildMessage(resolved)
        }
    }.build()

}
package cn.fd.ratziel.module.item.impl.feature.dynamic

import cn.fd.ratziel.common.message.Message
import cn.fd.ratziel.core.functional.ArgumentContext
import cn.fd.ratziel.core.functional.SimpleContext
import cn.fd.ratziel.module.item.ItemManager
import cn.fd.ratziel.module.item.impl.RatzielItem
import cn.fd.ratziel.module.item.impl.component.ItemDisplay
import cn.fd.ratziel.module.item.internal.nms.RefItemStack
import cn.fd.ratziel.module.item.util.writeTo
import cn.fd.ratziel.platform.bukkit.util.readOrThrow
import kotlinx.coroutines.*
import net.kyori.adventure.text.TextReplacementConfig
import org.bukkit.GameMode
import org.bukkit.event.player.PlayerGameModeChangeEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import taboolib.module.nms.MinecraftVersion
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
        when (event.packet.name) {
            "PacketPlayOutSetSlot", "ClientboundContainerSetSlotPacket" ->
                // 玩家模式不处理创造模式, 原因: 创造强制设置库存
                if (event.player.gameMode != GameMode.CREATIVE) handleSetSlotPacket(event)

            "PacketPlayOutWindowItems", "ClientboundContainerSetContentPacket" ->
                // 玩家模式不处理创造模式, 原因: 创造强制设置库存
                if (event.player.gameMode != GameMode.CREATIVE) handleWindowItemsPacket(event)
        }
    }

    private val itemStackFiledInSetSlotPacket = if (MinecraftVersion.isHigherOrEqual(MinecraftVersion.V1_17)) "itemStack" else "c"

    fun handleSetSlotPacket(event: PacketSendEvent) {
        val nmsItem = event.packet.readOrThrow<Any>(itemStackFiledInSetSlotPacket)
        handleItem(nmsItem, event)
    }

    private val itemsFieldInWindowItemsPacket = if (MinecraftVersion.isHigherOrEqual(MinecraftVersion.V1_17)) "items" else "b"

    private val carriedItemFieldInWindowItemsPacket = if (MinecraftVersion.isHigherOrEqual(MinecraftVersion.V1_17)) "carriedItem" else "c"

    fun handleWindowItemsPacket(event: PacketSendEvent) {
        val packetItems = event.packet.readOrThrow<List<Any>>(itemsFieldInWindowItemsPacket)
        runBlocking {
            // 处理每个物品
            val itemTasks = packetItems.map { launch { handleItem(it, event) } }

            // 处理手持物品 (carriedItem 在 1.12 后才有)
            if (MinecraftVersion.isHigherOrEqual(MinecraftVersion.V1_12)) {
                val packetCarriedItem = event.packet.readOrThrow<Any>(carriedItemFieldInWindowItemsPacket)
                handleItem(packetCarriedItem, event)
            }

            // 等待所有任务完成
            itemTasks.joinAll()
        }
    }

    /**
     * 处理物品
     *
     * @param nmsItem NMS 物品实例
     */
    fun handleItem(nmsItem: Any, event: PacketSendEvent) {
        val refItem = RefItemStack.ofNms(nmsItem)
        // 生成 RatzielItem 实例 (如果不是就跳过处理)
        val ratzielItem = RatzielItem.of(refItem.extractData()) ?: return

        // 生成上下文
        val cacheContext = ItemManager.getCacheContext(ratzielItem.identifier) // 缓存上下文
        val context = SimpleContext(ratzielItem, event.player, cacheContext)

        // 处理显示组件
        handleDisplay(ratzielItem, context)

        // 最后写回物品数据
        ratzielItem.writeTo(refItem.bukkitStack)
    }

    /**
     * 处理显示组件
     */
    fun handleDisplay(ratzielItem: RatzielItem, context: ArgumentContext) = runBlocking {
        // 读取显示组件
        val display = ratzielItem.getComponent(ItemDisplay::class.java)

        // 创建文本替换配置
        val replacementConfig = createReplacementConfig(context)

        // 显示名称处理
        val newName = display.name?.run { async { replaceText(replacementConfig) } }
        // 本地化名称处理
        val newLocalName = display.localizedName?.run { async { replaceText(replacementConfig) } }
        // Lore 处理
        val newLore = display.lore?.map { it.run { async { replaceText(replacementConfig) } } }

        // 创建新显示组件
        val newDisplay = ItemDisplay(
            newName?.await(),
            newLocalName?.await(),
            newLore?.awaitAll()
        )

        // 将新的组件写入物品
        ratzielItem.setComponent(newDisplay)
    }

    /**
     * 创建文本替换配置
     *
     * @param context 上下文
     * @return 文本替换配置
     */
    fun createReplacementConfig(context: ArgumentContext) = TextReplacementConfig.builder().apply {
        match(DynamicTagResolver.regex)
        replacement { text ->
            val resolved = DynamicTagResolver.resolveTag(text.content(), context)
                ?: return@replacement text // 如果解析失败, 返回原文本
            Message.buildMessage(resolved)
        }
    }.build()

}
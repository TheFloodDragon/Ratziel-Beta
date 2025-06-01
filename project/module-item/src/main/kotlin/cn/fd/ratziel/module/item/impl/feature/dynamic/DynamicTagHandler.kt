package cn.fd.ratziel.module.item.impl.feature.dynamic

import cn.fd.ratziel.common.message.Message
import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.core.function.SimpleContext
import cn.fd.ratziel.module.item.impl.RatzielItem
import cn.fd.ratziel.module.item.impl.component.ItemDisplay
import cn.fd.ratziel.module.item.internal.nms.RefItemStack
import cn.fd.ratziel.module.item.util.writeTo
import kotlinx.coroutines.*
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
        // 玩家模式仅处理 生存 和 冒险 模式, 原因: 创造强制设置库存, 旁观没什么必要
        fun checkGameMode() = event.player.gameMode == GameMode.SURVIVAL || event.player.gameMode == GameMode.ADVENTURE

        when (event.packet.name) {
            "PacketPlayOutSetSlot", "ClientboundContainerSetSlotPacket" ->
                if (checkGameMode()) handleSetSlotPacket(event)

            "PacketPlayOutWindowItems", "ClientboundContainerSetContentPacket" ->
                if (checkGameMode()) handleWindowItemsPacket(event)
        }
    }

    fun handleSetSlotPacket(event: PacketSendEvent) {
        // 获取物品 TODO 完善版本支持
        val nmsItem = event.packet.read<Any>("itemStack") ?: return
        handleItem(nmsItem, event)
    }

    fun handleWindowItemsPacket(event: PacketSendEvent) {
        // 获取物品 TODO 完善版本支持
        val packetItems = event.packet.read<List<Any>>("items") ?: return
        val packetCarriedItem = event.packet.read<Any>("carriedItem") ?: return

        // 处理每个物品
        runBlocking {
            // 创建任务
            val carriedItemTask = launch { handleItem(packetCarriedItem, event) }
            val itemTasks = packetItems.map { launch { handleItem(it, event) } }
            // 等待所有任务完成
            carriedItemTask.join()
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
        val context = SimpleContext(ratzielItem, event.player)

        // 处理显示组件
        handleDisplay(ratzielItem, context)

        // 最后写回物品数据
        ratzielItem.writeTo(refItem.bukkitStack)
    }

    /**
     * 处理显示组件
     */
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
                ?: return@replacement text
            Message.buildMessage(resolved)
        }
    }.build()

}
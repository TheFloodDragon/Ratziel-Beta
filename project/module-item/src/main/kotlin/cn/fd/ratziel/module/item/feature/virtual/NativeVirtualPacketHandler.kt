package cn.fd.ratziel.module.item.feature.virtual

import cn.fd.ratziel.core.functional.SimpleContext
import cn.fd.ratziel.module.item.ItemManager
import cn.fd.ratziel.module.item.impl.RatzielItem
import cn.fd.ratziel.module.item.internal.nms.RefItemStack
import cn.fd.ratziel.module.item.util.writeTo
import cn.fd.ratziel.platform.bukkit.util.readOrThrow
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.bukkit.entity.Player
import taboolib.common.platform.event.SubscribeEvent
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.PacketReceiveEvent
import taboolib.module.nms.PacketSendEvent
import java.util.function.BiConsumer
import java.util.function.Consumer

/**
 * NativeVirtualPacketHandler
 *
 * @author TheFloodDragon
 * @since 2025/8/3 11:41
 */
object NativeVirtualPacketHandler {

    @SubscribeEvent
    private fun onReceive(event: PacketReceiveEvent) {
        when (event.packet.name) {
            "PacketPlayInSetCreativeSlot", "ServerboundSetCreativeModeSlotPacket" -> handleSetCreativeSlot(event)
            "PacketPlayInWindowClick", "ServerboundContainerClickPacket" -> NMSVirtualItem.INSTANCE.handleContainerClick(event)
        }
    }

    private val itemStackFiledInSetCreativeSlotPacket = if (MinecraftVersion.isUniversal) "itemStack" else "b"

    fun handleSetCreativeSlot(event: PacketReceiveEvent) {
        val nmsItem = event.packet.readOrThrow<Any>(itemStackFiledInSetCreativeSlotPacket)
        // 恢复物品 (妈妈再也不用担心我的物品被摸坏了)
        handleItem(nmsItem) { NativeVirtualItemRenderer.recover(it) }
    }

    @SubscribeEvent
    private fun onSend(event: PacketSendEvent) {
        when (event.packet.name) {
            "PacketPlayOutSetSlot", "ClientboundContainerSetSlotPacket" -> handleSetSlotPacket(event)
            "PacketPlayOutWindowItems", "ClientboundContainerSetContentPacket" -> handleWindowItemsPacket(event)
            // 1.21.2 +
            "ClientboundSetCursorItemPacket" -> handleSetCursorItemPacket(event)
            "ClientboundSetPlayerInventoryPacket" -> handleSetPlayerInventoryPacket(event)
        }
    }

    fun handleSetCursorItemPacket(event: PacketSendEvent) {
        val nmsItem = event.packet.readOrThrow<Any>("contents")
        renderItem(nmsItem, event.player)
    }

    fun handleSetPlayerInventoryPacket(event: PacketSendEvent) {
        val nmsItem = event.packet.readOrThrow<Any>("contents")
        renderItem(nmsItem, event.player)
    }

    private val itemStackFiledInSetSlotPacket = if (MinecraftVersion.isUniversal) "itemStack" else "c"

    fun handleSetSlotPacket(event: PacketSendEvent) {
        val nmsItem = event.packet.readOrThrow<Any>(itemStackFiledInSetSlotPacket)
        renderItem(nmsItem, event.player)
    }

    private val itemsFieldInWindowItemsPacket = if (MinecraftVersion.isUniversal) "items" else "b"

    private val carriedItemFieldInWindowItemsPacket = if (MinecraftVersion.isUniversal) "carriedItem" else "c"

    fun handleWindowItemsPacket(event: PacketSendEvent) {
        val packetItems = event.packet.readOrThrow<List<Any>>(itemsFieldInWindowItemsPacket)
        runBlocking {
            // 处理每个物品
            val itemTasks = packetItems.map { launch { renderItem(it, event.player) } }

            // 处理手持物品 (carriedItem 在 1.12 后才有)
            if (MinecraftVersion.isHigherOrEqual(MinecraftVersion.V1_12)) {
                val packetCarriedItem = event.packet.readOrThrow<Any>(carriedItemFieldInWindowItemsPacket)
                renderItem(packetCarriedItem, event.player)
            }

            // 等待所有任务完成
            itemTasks.joinAll()
        }
    }

    /**
     * 渲染物品
     */
    fun renderItem(nmsItem: Any, player: Player) {
        this.handleItem(nmsItem, player) { item, context ->
            NativeVirtualItemRenderer.render(item, context)
        }
    }

    /**
     * 处理物品
     *
     * @param nmsItem NMS 物品实例
     */
    fun handleItem(nmsItem: Any, player: Player, consumer: BiConsumer<RatzielItem, SimpleContext>) {
        this.handleItem(nmsItem) { item ->
            // 生成上下文
            val context = SimpleContext(item, player)
            // 导入生成器的上下文
            val generator = ItemManager.registry[item.identifier.content]
            val args = generator?.contextProvider?.newContext()?.args()
            if (args != null) context.putAll(args)

            // 消费物品
            consumer.accept(item, context)
        }
    }

    /**
     * 处理物品
     *
     * @param nmsItem NMS 物品实例
     */
    fun handleItem(nmsItem: Any, consumer: Consumer<RatzielItem>) {
        val refItem = RefItemStack.ofNms(nmsItem)
        // 生成 RatzielItem 实例 (如果不是就跳过处理)
        val ratzielItem = RatzielItem.of(refItem.extractData()) ?: return
        // 消费物品
        consumer.accept(ratzielItem)

        // 最后写回物品数据
        ratzielItem.writeTo(refItem.bukkitStack)
    }

}
package cn.fd.ratziel.module.item.feature.virtual

import cn.fd.ratziel.module.item.impl.RatzielItem
import cn.fd.ratziel.module.item.internal.nms.RefItemStack
import cn.fd.ratziel.module.item.util.writeTo
import cn.fd.ratziel.platform.bukkit.util.readOrThrow
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.bukkit.entity.Player
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.severe
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.PacketReceiveEvent
import taboolib.module.nms.PacketSendEvent
import java.util.function.Consumer

/**
 * VirtualItemPacketHandler
 *
 * @author TheFloodDragon
 * @since 2025/8/3 11:41
 */
object VirtualItemPacketHandler {

    @SubscribeEvent
    private fun onReceive(event: PacketReceiveEvent) {
        try {
            when (event.packet.name) {
                "PacketPlayInSetCreativeSlot", "ServerboundSetCreativeModeSlotPacket" -> handleSetCreativeSlot(event)
                "PacketPlayInWindowClick", "ServerboundContainerClickPacket" -> handleContainerClick(event)
            }
        } catch (e: Throwable) {
            severe("Exception occurred while receiving packet: ${event.packet.name}")
            e.printStackTrace()
        }
    }

    private val itemStackFiledInSetCreativeSlotPacket = if (MinecraftVersion.isUniversal) "itemStack" else "b"

    fun handleSetCreativeSlot(event: PacketReceiveEvent) {
        val nmsItem = event.packet.readOrThrow<Any>(itemStackFiledInSetCreativeSlotPacket)
        // 恢复物品 (妈妈再也不用担心我的物品被摸坏了)
        handleItem(nmsItem) { NativeVirtualItemRenderer.recover(it) }
    }

    fun handleContainerClick(event: PacketReceiveEvent) {
        // 记得没错的话校验是 1.21.2 加的, 低版本就不处理了
        if (MinecraftVersion.versionId >= 12102) NMSVirtualItem.INSTANCE.handleContainerClick(event)
    }

    @SubscribeEvent
    private fun onSend(event: PacketSendEvent) {
        try {
            when (event.packet.name) {
                "PacketPlayOutSetSlot", "ClientboundContainerSetSlotPacket" -> handleSetSlotPacket(event)
                "PacketPlayOutWindowItems", "ClientboundContainerSetContentPacket" -> handleWindowItemsPacket(event)
                // 1.21.2 +
                "ClientboundSetCursorItemPacket" -> handleSetCursorItemPacket(event)
                "ClientboundSetPlayerInventoryPacket" -> handleSetPlayerInventoryPacket(event)
            }
        } catch (e: Throwable) {
            severe("Exception occurred while sending packet: ${event.packet.name}")
            e.printStackTrace()
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
    private fun renderItem(nmsItem: Any, player: Player) {
        this.handleItem(nmsItem) { item ->
            NativeVirtualItemRenderer.render(item, player)
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
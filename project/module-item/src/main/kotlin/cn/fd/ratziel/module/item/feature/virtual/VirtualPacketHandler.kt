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
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerGameModeChangeEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.PacketSendEvent

/**
 * VirtualPacketHandler
 *
 * @author TheFloodDragon
 * @since 2025/8/3 11:41
 */
object VirtualPacketHandler {

    @Deprecated("Will be remove since we wanna to support creative inventory")
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
        // 玩家模式不处理创造模式, 原因: 创造强制设置库存
        if (event.player.gameMode == GameMode.CREATIVE) return

        when (event.packet.name) {
            "PacketPlayOutSetSlot", "ClientboundContainerSetSlotPacket" -> handleSetSlotPacket(event)
            "PacketPlayOutWindowItems", "ClientboundContainerSetContentPacket" -> handleWindowItemsPacket(event)
            // 1.21.2 +
            "ClientboundSetCursorItemPacket" -> if (MinecraftVersion.versionId >= 12102) handleSetCursorItemPacket(event)
            "ClientboundSetPlayerInventoryPacket" -> if (MinecraftVersion.versionId >= 12102) handleSetPlayerInventoryPacket(event)
        }
    }

    private val itemStackFiledInSetSlotPacket = if (MinecraftVersion.isUniversal) "itemStack" else "c"

    fun handleSetSlotPacket(event: PacketSendEvent) {
        val nmsItem = event.packet.readOrThrow<Any>(itemStackFiledInSetSlotPacket)
        handleItem(nmsItem, event.player)
    }

    private val itemsFieldInWindowItemsPacket = if (MinecraftVersion.isUniversal) "items" else "b"

    private val carriedItemFieldInWindowItemsPacket = if (MinecraftVersion.isUniversal) "carriedItem" else "c"

    fun handleWindowItemsPacket(event: PacketSendEvent) {
        val packetItems = event.packet.readOrThrow<List<Any>>(itemsFieldInWindowItemsPacket)
        runBlocking {
            // 处理每个物品
            val itemTasks = packetItems.map { launch { handleItem(it, event.player) } }

            // 处理手持物品 (carriedItem 在 1.12 后才有)
            if (MinecraftVersion.isHigherOrEqual(MinecraftVersion.V1_12)) {
                val packetCarriedItem = event.packet.readOrThrow<Any>(carriedItemFieldInWindowItemsPacket)
                handleItem(packetCarriedItem, event.player)
            }

            // 等待所有任务完成
            itemTasks.joinAll()
        }
    }

    fun handleSetCursorItemPacket(event: PacketSendEvent) {
        val nmsItem = event.packet.readOrThrow<Any>("contents")
        handleItem(nmsItem, event.player)
    }

    fun handleSetPlayerInventoryPacket(event: PacketSendEvent) {
        val nmsItem = event.packet.readOrThrow<Any>("contents")
        handleItem(nmsItem, event.player)
    }

    /**
     * 处理物品
     *
     * @param nmsItem NMS 物品实例
     */
    fun handleItem(nmsItem: Any, player: Player) {
        val refItem = RefItemStack.ofNms(nmsItem)
        // 生成 RatzielItem 实例 (如果不是就跳过处理)
        val ratzielItem = RatzielItem.of(refItem.extractData()) ?: return

        // 生成上下文
        val context = SimpleContext(ratzielItem, player)
        // 导入生成器的上下文
        val generator = ItemManager.registry[ratzielItem.identifier.content]
        val args = generator?.contextProvider?.newContext()?.args()
        if (args != null) context.putAll(args)

        // 渲染物品
        NativeVirtualItemRenderer.render(ratzielItem, context)

        // 最后写回物品数据
        ratzielItem.writeTo(refItem.bukkitStack)
    }

}
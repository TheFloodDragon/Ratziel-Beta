package cn.fd.ratziel.module.ui.inventory.internal.nms

import cn.fd.ratziel.module.ui.inventory.internal.PlayerDragging
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.debug
import taboolib.module.nms.PacketReceiveEvent
import taboolib.module.nms.nmsProxy

/**
 * InventoryHandler
 *
 * @author TheFloodDragon
 * @since 2025/10/4 20:43
 */
abstract class InventoryHandler {

    companion object {

        @JvmStatic
        val INSTANCE by lazy {
            nmsProxy<InventoryHandler>()
        }

        /**
         * Only for test
         */
        @SubscribeEvent
        private fun onReceive(event: PacketReceiveEvent) {
            if (event.packet.name != "PacketPlayInWindowClick" && event.packet.name != "ServerboundContainerClickPacket") return

            val clickAction = InventoryClickPreprocessor.processClick(event.packet) {
                PlayerDragging[event.player.uniqueId]
            }

            debug(clickAction)
        }

    }

}
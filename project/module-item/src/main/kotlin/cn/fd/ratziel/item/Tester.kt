package cn.fd.ratziel.item

import cn.fd.ratziel.common.LogLevel
import cn.fd.ratziel.common.debug
import taboolib.common.platform.event.SubscribeEvent
import taboolib.module.nms.PacketReceiveEvent

/**
 * Tester
 *
 * @author TheFloodDragon
 * @since 2023/10/1 20:14
 */
object Tester {

    @SubscribeEvent
    fun onPacketReceive(event: PacketReceiveEvent) {
//        event.packet.source.let { packet ->
//            if (!packet.javaClass.name.contains("PacketPlayInPosition"))
//                debug(packet, LogLevel.Highest)
//        }
    }


}
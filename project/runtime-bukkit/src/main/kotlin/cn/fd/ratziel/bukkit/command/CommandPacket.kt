package cn.fd.ratziel.bukkit.command

import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.event.SubscribeEvent
import taboolib.expansion.createHelper
import taboolib.library.reflex.Reflex.Companion.getProperty
import taboolib.library.reflex.Reflex.Companion.invokeMethod
import taboolib.module.nms.PacketReceiveEvent
import taboolib.module.nms.PacketSendEvent

/**
 * CommandPacket
 *
 * @author TheFloodDragon
 * @since 2023/10/28 19:17
 */
@CommandHeader(
    name = "packet",
    permission = "ratziel.command.packet",
)
object CommandPacket {

    // 是否打印所有包类型
    var isPrintAll = false

    @CommandBody
    val main = mainCommand { createHelper() }

    @CommandBody
    val printAll = subCommand {
        execute<ProxyCommandSender> { sender, _, _ ->
            isPrintAll = !isPrintAll
            sender.sendMessage(isPrintAll.toString())
        }
    }


    @SubscribeEvent
    fun onPacketSend(e: PacketSendEvent) {
        val packet = e.packet
        if (isPrintAll && !packet.name.contains("PacketPlayOutEntity")
            && !packet.name.contains("PacketPlayOutRelEntityMove")) println(packet.source)

        // Test PacketPlayOutSetSlot
        if (packet.name == "PacketPlayOutSetSlot") {
            val nmsPacket = packet.source
            println(nmsPacket.getProperty<Int>("a"))
            println(nmsPacket.getProperty<Int>("b"))
            println(nmsPacket.getProperty<Int>("c"))
            println(nmsPacket.getProperty<Int>("d"))
            println(nmsPacket.getProperty<Int>("e"))
            val nmsItemStack = nmsPacket.getProperty<Any?>("f")!!
            println(nmsItemStack)
            val nmsTags = nmsItemStack.invokeMethod<Any?>("v")
            println(nmsTags)
        }
    }

    @SubscribeEvent
    fun onPacketReceive(e: PacketReceiveEvent) {
    }


}
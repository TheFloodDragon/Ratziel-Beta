package cn.fd.ratziel.bukkit.command

import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.event.SubscribeEvent
import taboolib.expansion.createHelper
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

    var listenSending = false

    var listenReceiving = false

    val filterNames = mutableListOf(
        "ClientboundBundlePacket",
        "PacketPlayOutUpdate",
        "PacketPlayOutEntity",
        "PacketPlayOutRelEntityMove",
        "PacketPlayOutUpdateTime"
    )

    @CommandBody
    val main = mainCommand { createHelper() }

    @CommandBody
    val toggle = subCommand {
        literal("sending") {
            execute<ProxyCommandSender> { sender, _, _ ->
                listenSending = !listenSending
                sender.sendMessage("Packet sending listening status: $listenSending")
            }
        }
        literal("receiving") {
            execute<ProxyCommandSender> { sender, _, _ ->
                listenReceiving = !listenReceiving
                sender.sendMessage("Packet receiving listening status: $listenSending")
            }
        }
    }

    @CommandBody
    val filter = subCommand {
        literal("get") {
            execute<ProxyCommandSender> { sender, _, _ -> sender.sendMessage("Filters: $filterNames") }
        }
        literal("add") {
            dynamic { execute<ProxyCommandSender> { _, _, name -> filterNames + name } }
        }
        literal("remove") {
            dynamic { execute<ProxyCommandSender> { _, _, name -> filterNames - name } }
        }
    }

    @SubscribeEvent
    fun onPacketSend(e: PacketSendEvent) {
        if (!listenSending) return
        filterNames.forEach { if (e.packet.fullyName.contains(it)) return }
        printPacket(e.packet.source)
    }

    @SubscribeEvent
    fun onPacketReceive(e: PacketReceiveEvent) {
        if (!listenReceiving) return
        filterNames.forEach { if (e.packet.fullyName.contains(it)) return }
        printPacket(e.packet.source)
    }

    private fun printPacket(source: Any) {
        println("Packet: $source")
        source::class.java.declaredFields.forEach { field ->
            val fieldValue = field.apply { isAccessible = true }.get(source)
            println("      ${field.name}=$fieldValue")
        }
    }

}
package cn.fd.ratziel.platform.bukkit.command

import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.event.SubscribeEvent
import taboolib.expansion.createHelper
import taboolib.module.nms.Packet
import taboolib.module.nms.PacketReceiveEvent
import taboolib.module.nms.PacketSendEvent
import java.lang.reflect.Field


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
        "PacketPlayInPosition",
        "PacketPlayInLook",
        "ClientboundLight",
        "ClientboundLevelChunk",
        "PacketPlayOutUnloadChunk",
        "PacketPlayOutEntityHeadRotation",
        "PacketPlayOutRelEntityMove",
        "PacketPlayOutEntityTeleport",
        "PacketPlayOutEntityVelocity",
        "PacketPlayOutEntityDestroy",
        "PacketPlayOutEntityStatus",
        "PacketPlayOutUpdateTime",
        "PacketPlayOutUpdateAttributes",
        "PacketPlayOutEntityLook",
        "PacketPlayOutEntityMetadata",
        "KeepAlivePacket",
        "PacketPlayOutNamedSoundEffect",
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
                sender.sendMessage("Packet receiving listening status: $listenReceiving")
            }
        }
    }

    @CommandBody
    val filter = subCommand {
        literal("get") {
            execute<ProxyCommandSender> { sender, _, _ -> sender.sendMessage("Filters: $filterNames") }
        }
        literal("add") {
            dynamic { execute<ProxyCommandSender> { _, _, name -> filterNames.add(name) } }
        }
        literal("remove") {
            dynamic { execute<ProxyCommandSender> { _, _, name -> filterNames.remove(name) } }
        }
    }

    @SubscribeEvent
    fun onPacketSend(e: PacketSendEvent) {
        if (!listenSending) return
        printPacket(e.packet)
    }

    @SubscribeEvent
    fun onPacketReceive(e: PacketReceiveEvent) {
        if (!listenReceiving) return
        printPacket(e.packet)
    }

    private fun printPacket(packet: Packet) {
        if (filterNames.any { packet.fullyName.contains(it) }) return
        println("Packet: ${packet.source}")
        getAllFields(packet.source::class.java).forEach { field ->
            val fieldValue = field.apply { isAccessible = true }.get(packet.source)
            println("      ${field.name}=$fieldValue")
        }
    }

    private fun getAllFields(clazz: Class<*>?) = buildList<Field> {
        var target = clazz
        while (target != null) {
            addAll(target.declaredFields)
            target = target.superclass
        }
    }

}
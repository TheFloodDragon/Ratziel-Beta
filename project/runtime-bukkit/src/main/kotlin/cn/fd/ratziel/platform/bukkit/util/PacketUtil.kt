package cn.fd.ratziel.platform.bukkit.util

import taboolib.module.nms.Packet

/**
 * PacketUtil
 *
 * @author TheFloodDragon
 * @since 2025/6/1 11:19
 */

fun <T> Packet.readOrThrow(name: String, remap: Boolean = true) =
    read<T>(name, remap) ?: throw NoSuchFieldException("Field '$name' not found in packet ${this.name}")

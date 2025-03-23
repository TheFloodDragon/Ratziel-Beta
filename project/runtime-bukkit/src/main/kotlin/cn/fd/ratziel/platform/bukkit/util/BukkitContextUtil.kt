package cn.fd.ratziel.platform.bukkit.util

import cn.fd.ratziel.core.function.ArgumentContext
import org.bukkit.OfflinePlayer
import taboolib.common.platform.ProxyPlayer

fun ArgumentContext.player() = popOrNull(ProxyPlayer::class.java)?.cast() ?: popOrNull(OfflinePlayer::class.java)
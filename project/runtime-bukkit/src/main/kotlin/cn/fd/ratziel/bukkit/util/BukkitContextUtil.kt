package cn.fd.ratziel.bukkit.util

import cn.fd.ratziel.function.ArgumentContext
import cn.fd.ratziel.function.popOrNull
import org.bukkit.OfflinePlayer
import taboolib.common.platform.ProxyPlayer

fun ArgumentContext.player() = popOrNull<ProxyPlayer>()?.cast() ?: popOrNull<OfflinePlayer>()
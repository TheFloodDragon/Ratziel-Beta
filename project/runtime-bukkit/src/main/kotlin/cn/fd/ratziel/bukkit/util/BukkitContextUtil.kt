package cn.fd.ratziel.bukkit.util

import cn.fd.ratziel.function.ArgumentContext
import cn.fd.ratziel.function.getOrNull
import org.bukkit.OfflinePlayer
import taboolib.common.platform.ProxyPlayer

fun ArgumentContext.player() = getOrNull<ProxyPlayer>()?.cast() ?: getOrNull<OfflinePlayer>()
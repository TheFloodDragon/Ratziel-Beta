package cn.fd.ratziel.platform.bukkit.util

import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.core.function.popOrNull
import org.bukkit.OfflinePlayer
import taboolib.common.platform.ProxyPlayer

fun ArgumentContext.player() = popOrNull<ProxyPlayer>()?.cast() ?: popOrNull<OfflinePlayer>()
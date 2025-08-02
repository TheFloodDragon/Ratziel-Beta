package cn.fd.ratziel.platform.bukkit.util

import cn.fd.ratziel.core.functional.ArgumentContext
import cn.fd.ratziel.module.script.impl.VariablesMap
import cn.fd.ratziel.module.script.util.varsMap
import org.bukkit.OfflinePlayer
import taboolib.common.platform.Awake
import taboolib.common.platform.ProxyPlayer

/**
 * 从上下文中获取玩家对象
 */
fun ArgumentContext.player() = popOrNull(ProxyPlayer::class.java)?.cast() ?: popOrNull(OfflinePlayer::class.java)

/***
 * 注册 [VariablesMap.transformers]
 */
@Awake
private fun transformer() {
    VariablesMap.transformers.add {
        val player = it.player() ?: return@add
        it.varsMap().put("player", player)
    }
}

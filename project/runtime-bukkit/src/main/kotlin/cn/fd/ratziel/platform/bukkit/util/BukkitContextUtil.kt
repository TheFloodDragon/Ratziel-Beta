package cn.fd.ratziel.platform.bukkit.util

import cn.fd.ratziel.core.contextual.ArgumentContext
import cn.fd.ratziel.module.script.util.VariablesMap
import org.bukkit.OfflinePlayer
import taboolib.common.platform.Awake
import taboolib.common.platform.ProxyPlayer

/**
 * 从上下文中获取玩家对象
 */
fun ArgumentContext.player() = popOrNull(OfflinePlayer::class.java) ?: popOrNull(ProxyPlayer::class.java)?.cast()

/***
 * 注册 [VariablesMap.transformers]
 */
@Awake
private fun transformer() {
    VariablesMap.transformers.add { varsMap, context ->
        val player = context.player() ?: return@add
        varsMap["player"] = player
    }
}

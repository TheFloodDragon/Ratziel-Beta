package cn.fd.ratziel.kether.bacikal.applicative

import org.bukkit.Bukkit
import org.bukkit.entity.Player

/**
 * @author Lanscarlos
 * @since 2023-08-21 14:53
 */
class PlayerListApplicative(source: Any) : AbstractApplicative<List<Player>>(source) {

    val applicative = PlayerApplicative(-1)

    override fun transfer(source: Any, def: List<Player>?): List<Player>? {
        return when (source) {
            "*" -> {
                // 所有玩家
                Bukkit.getOnlinePlayers().toList()
            }
            is List<*> -> {
                source.mapNotNull { applicative.transfer(it ?: return@mapNotNull null, null) }
            }
            is Array<*> -> {
                source.mapNotNull { applicative.transfer(it ?: return@mapNotNull null, null) }
            }
            else -> def
        }
    }

    companion object {
        fun Any.applicativePlayerList() = PlayerListApplicative(this)
    }
}
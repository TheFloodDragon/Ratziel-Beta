package cn.fd.ratziel.kether.bacikal.applicative

import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Entity
import taboolib.common.platform.ProxyPlayer
import taboolib.platform.type.BukkitPlayer

/**
 * @author Lanscarlos
 * @since 2023-08-21 15:10
 */
class EntityApplicative(source: Any) : AbstractApplicative<Entity>(source) {

    override fun transfer(source: Any, def: Entity?): Entity? {
        return when (source) {
            is Entity -> source
            is OfflinePlayer -> source.player
            is ProxyPlayer -> (source as? BukkitPlayer)?.player
            is String -> Bukkit.getPlayerExact(source)
            else -> def
        }
    }

    companion object {

        fun Any.applicativeEntity() = EntityApplicative(this)
    }
}
package cn.fd.ratziel.function.argument

import cn.fd.ratziel.core.exception.UnsupportedTypeException
import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.function.adaptPlayer
import org.bukkit.entity.Player as BukkitPlayer

/**
 * PlayerArgument
 *
 * @author TheFloodDragon
 * @since 2024/5/1 13:42
 */
class PlayerArgument<T>(unsureValue: T) : Argument<ProxyPlayer> where T : BukkitPlayer, T : ProxyPlayer {

    override val type: Class<*> = unsureValue::class.java

    override val value: ProxyPlayer =
        when (unsureValue::class.java) {
            ProxyPlayer::class.java -> unsureValue
            BukkitPlayer::class.java -> adaptPlayer(unsureValue)
            else -> throw UnsupportedTypeException(unsureValue)
        }

}
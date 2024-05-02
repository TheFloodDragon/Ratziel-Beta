package cn.fd.ratziel.function.argument

import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.function.adaptPlayer

/**
 * PlayerArgument
 *
 * @author TheFloodDragon
 * @since 2024/5/1 18:24
 */
class PlayerArgument(value: ProxyPlayer) : SuppliableArgument<ProxyPlayer>(value) {

    constructor(unsure: Any) : this(if (unsure is ProxyPlayer) unsure else adaptPlayer(unsure))

    override fun <T> supply(type: Class<T>): T =
        if (ProxyPlayer::class.java.isAssignableFrom(type)) uncheck(value) else value.cast()

}
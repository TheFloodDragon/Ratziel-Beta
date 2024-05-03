package cn.fd.ratziel.function.argument

import cn.fd.ratziel.function.util.uncheck
import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.function.adaptPlayer

/**
 * PlayerArgument
 *
 * @author TheFloodDragon
 * @since 2024/5/1 18:24
 */
class PlayerArgument(value: ProxyPlayer) : SuppliableArgument<ProxyPlayer>(value) {

    constructor(value: Any) : this(if (value is ProxyPlayer) value else adaptPlayer(value))

    override fun <T : Any> supply(type: Class<T>): Argument<T> = uncheck(
        if (ProxyPlayer::class.java.isAssignableFrom(type)) this // 需求ProxyPlayer时直接返回
        else PlayerArgument(this.value.cast<T>()) // 否则强制并返回  (单个平台不能有多个Player吧)
    )

}
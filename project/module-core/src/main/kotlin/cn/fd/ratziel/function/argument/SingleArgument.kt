package cn.fd.ratziel.function.argument

/**
 * SingleArgument
 *
 * @author TheFloodDragon
 * @since 2024/5/1 13:22
 */
data class SingleArgument<T : Any>(
    override val value: T,
    override val type: Class<*>
) : Argument<T> {

    constructor(value: T) : this(value, value::class.java)

}
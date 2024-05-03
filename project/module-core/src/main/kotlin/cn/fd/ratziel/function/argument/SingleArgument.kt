package cn.fd.ratziel.function.argument

/**
 * SingleArgument
 *
 * @author TheFloodDragon
 * @since 2024/5/1 13:22
 */
open class SingleArgument<out T : Any>(override val value: T) : Argument<T> {

    override fun hashCode() = value.hashCode()

    override fun equals(other: Any?) = super.equals(other) || (other is SingleArgument<*> && value == other.value)

    override fun toString() = "SingleArgument(value=$value, type=$type)"

}
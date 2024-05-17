package cn.fd.ratziel.function.argument

/**
 * SingleArgument
 *
 * @author TheFloodDragon
 * @since 2024/5/1 13:22
 */
open class SingleArgument<out V : Any>(override val value: V) : Argument<V> {

    override val type: Class<*> get() = value::class.java

    override fun hashCode() = value.hashCode()

    override fun equals(other: Any?) = super.equals(other) || (other is SingleArgument<*> && value == other.value)

    override fun toString() = "SingleArgument(value=$value, type=$type)"

}
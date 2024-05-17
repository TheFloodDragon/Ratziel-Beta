package cn.fd.ratziel.function.argument

/**
 * NamedArgument
 *
 * @author TheFloodDragon
 * @since 2024/5/17 21:45
 */
open class NamedArgument<V : Any>(val name: String, value: V) : SingleArgument<V>(value) {

    override fun toString() = "NamedArgument(name=$name, value=$value, type=$type)"

}
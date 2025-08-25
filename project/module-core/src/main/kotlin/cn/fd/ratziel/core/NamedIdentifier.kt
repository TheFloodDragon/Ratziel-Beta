package cn.fd.ratziel.core

/**
 * NamedIdentifier
 *
 * @author TheFloodDragon
 * @since 2024/6/24 13:47
 */
@JvmInline
value class NamedIdentifier(override val content: String) : Identifier {

    override fun toString() = this.content

}
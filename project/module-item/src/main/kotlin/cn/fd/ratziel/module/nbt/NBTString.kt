package cn.fd.ratziel.module.nbt

/**
 * NBTString
 *
 * @author TheFloodDragon
 * @since 2024/3/15 21:13
 */
@JvmInline
value class NBTString(override val content: String) : NBTData{

    override fun clone() = NBTString(content)

    override fun toString() = content

    override val type get() = NBTType.STRING

}
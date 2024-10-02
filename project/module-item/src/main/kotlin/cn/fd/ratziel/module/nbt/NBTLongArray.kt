package cn.fd.ratziel.module.nbt

/**
 * NBTLongArray
 *
 * @author TheFloodDragon
 * @since 2024/3/15 21:23
 */
@JvmInline
value class NBTLongArray(override val content: LongArray) : NBTData {

    override fun clone() = NBTLongArray(content)

    override fun toString() = content.toString()

    override val type get() = NBTType.LONG_ARRAY

}
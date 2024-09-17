package cn.fd.ratziel.module.nbt

/**
 * NBTIntArray
 *
 * @author TheFloodDragon
 * @since 2024/3/15 21:22
 */
@JvmInline
value class NBTIntArray(override val content: IntArray) : NBTData {

    override fun clone() = NBTIntArray(content.copyOf())

    override val type get() = NBTType.INT_ARRAY

}
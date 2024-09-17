package cn.fd.ratziel.module.nbt

/**
 * NBTShort
 *
 * @author TheFloodDragon
 * @since 2024/3/15 21:24
 */
@JvmInline
value class NBTShort(override val content: Short) : NBTData{

    override fun clone() = NBTShort(content)

    override val type get() = NBTType.SHORT

}
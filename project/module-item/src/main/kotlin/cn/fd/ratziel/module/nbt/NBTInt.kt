package cn.fd.ratziel.module.nbt

/**
 * NBTInt
 *
 * @author TheFloodDragon
 * @since 2024/3/15 21:14
 */
@JvmInline
value class NBTInt(override val content: Int) : NBTData {

    override fun clone() = NBTInt(content)

    override fun toString() = content.toString()

    override val type get() = NBTType.INT

}
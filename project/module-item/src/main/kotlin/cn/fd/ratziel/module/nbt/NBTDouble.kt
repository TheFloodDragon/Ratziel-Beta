package cn.fd.ratziel.module.nbt

/**
 * NBTDouble
 *
 * @author TheFloodDragon
 * @since 2024/3/15 21:18
 */
@JvmInline
value class NBTDouble(override val content: Double) : NBTData{

    override fun clone() = NBTDouble(content)

    override fun toString() = content.toString()

    override val type get() = NBTType.DOUBLE

}
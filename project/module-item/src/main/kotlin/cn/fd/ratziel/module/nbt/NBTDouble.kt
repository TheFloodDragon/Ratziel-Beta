package cn.fd.ratziel.module.nbt

/**
 * NBTDouble
 *
 * @author TheFloodDragon
 * @since 2024/3/15 21:18
 */
class NBTDouble(override val content: Double) : NBTData(NBTType.DOUBLE) {

    override fun clone() = NBTDouble(content)

}
package cn.fd.ratziel.module.nbt

/**
 * NBTInt
 *
 * @author TheFloodDragon
 * @since 2024/3/15 21:14
 */
class NBTInt(override val content: Int) : NBTData(NBTType.INT) {

    override fun clone() = NBTInt(content)

}
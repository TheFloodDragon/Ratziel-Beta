package cn.fd.ratziel.module.nbt

/**
 * NBTShort
 *
 * @author TheFloodDragon
 * @since 2024/3/15 21:24
 */
class NBTShort(override val content: Short) : NBTData(NBTType.SHORT) {

    override fun clone() = NBTShort(content)

}
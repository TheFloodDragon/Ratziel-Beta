package cn.fd.ratziel.module.nbt

/**
 * NBTLongArray
 *
 * @author TheFloodDragon
 * @since 2024/3/15 21:23
 */
class NBTLongArray(override val content: LongArray) : NBTData(NBTType.LONG_ARRAY) {

    override fun clone() = NBTLongArray(content)

}
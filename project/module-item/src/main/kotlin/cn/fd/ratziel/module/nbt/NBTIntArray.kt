package cn.fd.ratziel.module.nbt

/**
 * NBTIntArray
 *
 * @author TheFloodDragon
 * @since 2024/3/15 21:22
 */
class NBTIntArray(override val content: IntArray) : NBTData(NBTType.INT_ARRAY) {

    override fun clone() = NBTIntArray(content)

}
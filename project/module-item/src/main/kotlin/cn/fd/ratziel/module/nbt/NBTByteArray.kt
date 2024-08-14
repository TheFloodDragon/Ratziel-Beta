package cn.fd.ratziel.module.nbt

/**
 * NBTByteArray
 *
 * @author TheFloodDragon
 * @since 2024/3/15 21:21
 */
class NBTByteArray(override val content: ByteArray) : NBTData(NBTType.BYTE_ARRAY) {

    override fun clone() = NBTByteArray(content)

}
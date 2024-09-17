package cn.fd.ratziel.module.nbt

/**
 * NBTByteArray
 *
 * @author TheFloodDragon
 * @since 2024/3/15 21:21
 */
@JvmInline
value class NBTByteArray(override val content: ByteArray) : NBTData {

    override fun clone() = NBTByteArray(content)

    override val type get() = NBTType.BYTE_ARRAY

}
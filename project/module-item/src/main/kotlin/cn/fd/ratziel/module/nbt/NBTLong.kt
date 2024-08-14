package cn.fd.ratziel.module.nbt

/**
 * NBTLong
 *
 * @author TheFloodDragon
 * @since 2024/3/15 21:22
 */
class NBTLong(override val content: Long) : NBTData(NBTType.LONG) {

    override fun clone() = NBTLong(content)

    override fun toString() = content.toString() + "l"

}
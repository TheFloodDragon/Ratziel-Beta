package cn.fd.ratziel.module.nbt

/**
 * NBTFloat
 *
 * @author TheFloodDragon
 * @since 2024/3/15 21:23
 */
class NBTFloat(override val content: Float) : NBTData(NBTType.FLOAT) {

    override fun clone() = NBTFloat(content)

    override fun toString() = content.toString() + "f"

}
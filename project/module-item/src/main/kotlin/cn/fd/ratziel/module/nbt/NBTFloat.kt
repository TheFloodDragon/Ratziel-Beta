package cn.fd.ratziel.module.nbt

/**
 * NBTFloat
 *
 * @author TheFloodDragon
 * @since 2024/3/15 21:23
 */
@JvmInline
value class NBTFloat(override val content: Float) : NBTData {

    override fun clone() = NBTFloat(content)

    override fun toString() = content.toString() + "f"

    override val type get() = NBTType.FLOAT

}
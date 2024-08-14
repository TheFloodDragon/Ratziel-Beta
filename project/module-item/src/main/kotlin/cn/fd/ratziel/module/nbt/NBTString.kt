package cn.fd.ratziel.module.nbt

/**
 * NBTString
 *
 * @author TheFloodDragon
 * @since 2024/3/15 21:13
 */
class NBTString(override val content: String) : NBTData(NBTType.STRING) {

    override fun clone() = NBTString(content)

}
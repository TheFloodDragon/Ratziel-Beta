package cn.fd.ratziel.module.itemengine.nbt

/**
 * NBTBoolean - 附加NBT类 (即原版都没有的)
 *
 * @author TheFloodDragon
 * @since 2023/11/24 22:27
 */
open class NBTBoolean(val value: Boolean) : NBTByte(if (value) BYTE_TRUE else BYTE_FALSE) {

    companion object {
        const val BYTE_FALSE: Byte = 0
        const val BYTE_TRUE: Byte = 1
    }

}
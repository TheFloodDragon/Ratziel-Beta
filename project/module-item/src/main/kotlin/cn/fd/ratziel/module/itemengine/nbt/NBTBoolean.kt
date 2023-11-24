package cn.fd.ratziel.module.itemengine.nbt

/**
 * NBTBoolean - 附加NBT类 (即原版都没有的)
 *
 * @author TheFloodDragon
 * @since 2023/11/24 22:27
 */

open class NBTBoolean(val value: Boolean) : NBTByte(if (value) byteTrue else byteFalse) {

    companion object {
        const val byteFalse: Byte = 0
        const val byteTrue: Byte = 1
    }

}
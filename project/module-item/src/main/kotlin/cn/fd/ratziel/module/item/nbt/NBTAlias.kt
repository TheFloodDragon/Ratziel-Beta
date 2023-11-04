package cn.fd.ratziel.module.item.nbt

import taboolib.library.reflex.Reflex.Companion.invokeConstructor
import taboolib.module.nms.*

typealias NBTType = ItemTagType
typealias NBTData = ItemTagData
typealias NBTTag = ItemTag
typealias NBTList = ItemTagList

/**
 * NBT各种类型的封装
 * 注意: 请勿使用NBTTagData原有的获取数据方法
 */

open class NBTCompound(data: NBTTag) : NBTData(NBTType.COMPOUND, data) {
    companion object {
        /**
         * NBTTagCompound
         *   1.17+ net.minecraft.nbt.NBTTagCompound
         *   1.17- net.minecraft.server.$VERSION.NBTTagCompound
         */
        @JvmStatic
        val clazz by lazy {
            if (MinecraftVersion.isHigherOrEqual(MinecraftVersion.V1_17))
                Class.forName("net.minecraft.nbt.NBTTagCompound")
            else nmsClass("NBTTagCompound")
        }

        /**
         * NBTTagCompound#constructor()
         */
        fun createInstance() = clazz.invokeConstructor()
    }

    fun getData() = asCompound()
}

open class NBTByte(data: Byte) : NBTData(NBTType.BYTE, data) {
    fun getData() = asByte()
}

const val bFalse: Byte = 0
const val bTrue: Byte = 1

open class NBTBoolean(private val bl: Boolean) : NBTByte(if (bl) bTrue else bFalse) {
    fun getBoolean() = bl
}

open class NBTShort(data: Short) : NBTData(NBTType.SHORT, data) {
    fun getData() = asShort()
}

open class NBTInt(data: Int) : NBTData(NBTType.INT, data) {
    fun getData() = asInt()
}

open class NBTIntArray(data: IntArray) : NBTData(NBTType.INT_ARRAY, data) {
    fun getData() = asIntArray()
}

open class NBTLong(data: Long) : NBTData(NBTType.LONG, data) {
    fun getData() = asLong()
}

open class NBTFloat(data: Float) : NBTData(NBTType.FLOAT, data) {
    fun getData() = asFloat()
}

open class NBTDouble(data: Double) : NBTData(NBTType.DOUBLE, data) {
    fun getData() = asDouble()
}

open class NBTByteArray(data: ByteArray) : NBTData(NBTType.BYTE_ARRAY, data) {
    fun getData() = asByteArray()
}

class NBTString(data: String) : NBTData(NBTType.STRING, data) {
    fun getData() = asString()
}
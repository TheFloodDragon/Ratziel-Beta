package cn.fd.ratziel.module.item.util.nbt

import taboolib.library.reflex.Reflex.Companion.invokeConstructor
import taboolib.module.nms.*

typealias NBTType = ItemTagType
typealias NBTData = ItemTagData
typealias NBTTag = ItemTag
typealias NBTTagList = ItemTagList

/**
 * NBT各种类型的封装
 * 注意: 请勿使用NBTTagData原有的获取数据方法
 */

abstract class PackagedNBT(
    /**
     * 包装数据形式
     */
    open val data: NBTData,
) {

    /**
     * 获取NMS形式
     */
    open fun getNMS() = data.toNMS()
}

open class NBTCompound(rawData: NBTTag) : PackagedNBT(rawData) {

    constructor(nmsData: Any) : this(nbtFromNMS(nmsData) as NBTTag)

    override var data: NBTTag = super.data as NBTTag

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
        @JvmStatic
        fun new() = clazz.invokeConstructor()
    }

}

open class NBTList(rawData: NBTTagList) : PackagedNBT(rawData) {
    constructor(dataList: List<NBTData>) : this(NBTTagList(dataList))

    constructor(nmsData: Any) : this(nbtFromNMS(nmsData) as NBTTagList)

    override var data: NBTTagList = super.data as NBTTagList
}

open class NBTByte(rawData: Byte) : PackagedNBT(NBTData(NBTType.BYTE, rawData)) {
    constructor(nmsData: Any) : this(nbtFromNMS(nmsData).asByte())
}

open class NBTByteArray(rawData: ByteArray) : PackagedNBT(NBTData(NBTType.BYTE_ARRAY, rawData)) {
    constructor(nmsData: Any) : this(nbtFromNMS(nmsData).asByteArray())
}

const val bFalse: Byte = 0
const val bTrue: Byte = 1

open class NBTBoolean(rawData: Boolean) : PackagedNBT(NBTData(NBTType.BYTE, if (rawData) bTrue else bFalse)) {
    fun getBoolean() = data.asByte() == bTrue
}

open class NBTShort(rawData: Short) : PackagedNBT(NBTData(NBTType.SHORT, rawData)) {
    constructor(nmsData: Any) : this(nbtFromNMS(nmsData).asShort())
}

open class NBTInt(rawData: Int) : PackagedNBT(NBTData(NBTType.INT, rawData)) {
    constructor(nmsData: Any) : this(nbtFromNMS(nmsData).asInt())
}

open class NBTIntArray(rawData: IntArray) : PackagedNBT(NBTData(NBTType.INT_ARRAY, rawData)) {
    constructor(nmsData: Any) : this(nbtFromNMS(nmsData).asIntArray())
}

open class NBTLong(rawData: Long) : PackagedNBT(NBTData(NBTType.LONG, rawData)) {
    constructor(nmsData: Any) : this(nbtFromNMS(nmsData).asLong())
}

// TODO 等待Taboolib更新
//open class NBTLongArray(rawData: LongArray) : PackagedNBT(NBTData(NBTType.LONG_ARRAY, rawData)) {
//    constructor(nmsData: Any) : this(nbtFromNMS(nmsData).asLongArray())
//}

open class NBTFloat(rawData: Float) : PackagedNBT(NBTData(NBTType.FLOAT, rawData)) {
    constructor(nmsData: Any) : this(nbtFromNMS(nmsData).asFloat())
}

open class NBTDouble(rawData: Double) : PackagedNBT(NBTData(NBTType.DOUBLE, rawData)) {
    constructor(nmsData: Any) : this(nbtFromNMS(nmsData).asDouble())
}

open class NBTString(rawData: String) : PackagedNBT(NBTData(NBTType.STRING, rawData)) {
    constructor(nmsData: Any) : this(nbtFromNMS(nmsData).asString())
}
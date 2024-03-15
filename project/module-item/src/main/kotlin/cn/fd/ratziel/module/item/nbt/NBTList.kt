package cn.fd.ratziel.module.item.nbt

import cn.fd.ratziel.core.exception.UnsupportedTypeException

/**
 * NBTList
 *
 * @author TheFloodDragon
 * @since 2024/3/15 20:31
 */
@Suppress("UNCHECKED_CAST")
class NBTList(rawData: Any) : NBTData(rawData, NBTType.LIST) {

    constructor() : this(new())

    init {
        if (!isOwnNmsClass(rawData::class.java)) throw UnsupportedTypeException(rawData)
    }

    internal val sourceList get() = NMSUtil.NtList.sourceField.get(data) as List<Any>

    val content: List<NBTData> get() = sourceList.map { NBTConverter.convert(it) }

    companion object {

        fun new() = new(ArrayList())

        fun new(list: ArrayList<Any>) = NMSUtil.NtList.constructor.instance(list)!!

        fun isOwnNmsClass(clazz: Class<*>) = NMSUtil.NtList.nmsClass.isAssignableFrom(clazz::class.java)

    }

}
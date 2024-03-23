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

    internal val sourceList get() = NMSUtil.NtList.sourceField.get(data) as ArrayList<Any>

    val content: List<NBTData> get() = sourceList.map { NBTConverter.convert(it) }

    /**
     * 获取数据
     */
    operator fun get(index: Int): NBTData? = try {
        NBTList(sourceList[index])
    } catch (_: IndexOutOfBoundsException) {
        null
    }

    /**
     * 在索引处添加数据
     */
    fun add(index: Int, value: NBTData) = try {
        sourceList.add(index, value.getData())
    } catch (_: IndexOutOfBoundsException) {
        null
    }

    /**
     * 在末尾添加数据
     */
    fun add(value: NBTData) = sourceList.add(value.getData())

    /**
     * 根据索引删除数据
     */
    fun remove(index: Int) {
        sourceList.removeAt(index)
    }

    /**
     * 删除某个指定数据
     */
    fun remove(value: NBTData) = sourceList.remove(value.getData())

    /**
     * 设置索引处的数据
     */
    fun set(index: Int, value: NBTData) = sourceList.set(index, value.getData())

    fun setCreatable(index: Int, value: NBTData) = if (index == sourceList.size) add(value) else set(index, value)

    /**
     * 克隆数据
     */
    fun clone() = this.apply {
        data = NMSUtil.NtList.methodClone.invoke(data)!!
    }

    companion object {

        fun new() = new(ArrayList())

        fun new(list: ArrayList<Any>) = NMSUtil.NtList.constructor.instance(list)!!

        fun isOwnNmsClass(clazz: Class<*>) = NMSUtil.NtList.nmsClass.isAssignableFrom(clazz::class.java)

    }

}
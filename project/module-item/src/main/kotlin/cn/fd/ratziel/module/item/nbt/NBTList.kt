package cn.fd.ratziel.module.item.nbt

import cn.fd.ratziel.core.exception.UnsupportedTypeException
import cn.fd.ratziel.function.uncheck

/**
 * NBTList
 *
 * @author TheFloodDragon
 * @since 2024/3/15 20:31
 */
open class NBTList private constructor(rawData: Any) : NBTData(rawData, NBTType.LIST), MutableList<NBTData> {

    constructor() : this(new())

    constructor(list: Iterable<*>) : this(NBTAdapter.adaptList(list).getRaw())

    /**
     * [java.util.List] is the same as [MutableList] in [kotlin.collections]
     * Because [MutableList] will be compiled to [java.util.List]
     * And this is final, so it's not getter
     */
    internal val sourceList: MutableList<Any> = uncheck(NMSUtil.NtList.sourceField.get(data)!!)

    /**
     * 内容
     */
    override val content: List<NBTData> get() = sourceList.map { NBTAdapter.adaptNms(it) }

    /**
     * 获取数据
     */
    override operator fun get(index: Int): NBTData = sourceList[index].let { NBTAdapter.adaptNms(it) }

    /**
     * 在索引处添加数据
     */
    override fun add(index: Int, element: NBTData) = sourceList.add(index, element.getRaw())

    /**
     * 在末尾添加数据
     */
    override fun add(element: NBTData) = sourceList.add(element.getRaw())

    /**
     * 根据索引删除数据
     */
    override fun removeAt(index: Int): NBTData = sourceList.removeAt(index).let { NBTAdapter.adaptNms(it) }

    /**
     * 删除某个指定数据
     */
    override fun remove(element: NBTData) = sourceList.remove(element.getRaw())

    /**
     * 设置索引处的数据
     */
    override fun set(index: Int, element: NBTData): NBTData = sourceList.set(index, element.getRaw()).let { NBTAdapter.adaptNms(it) }

    /**
     * 克隆数据
     */
    open fun clone() = this.apply { data = NMSUtil.NtList.methodClone.invoke(data)!! }

    companion object {

        @JvmStatic
        fun new() = new(ArrayList())

        @JvmStatic
        fun new(list: List<Any>) = NMSUtil.NtList.constructor.instance(list, 0.toByte())!!

        @JvmStatic
        fun of(raw: Any) = if (NMSUtil.NtList.isOwnClass(raw::class.java)) NBTList(raw) else throw UnsupportedTypeException(raw)

    }

    /**
     * 列表是否为空
     */
    override fun isEmpty(): Boolean = sourceList.isEmpty()

    override fun indexOf(element: NBTData): Int = sourceList.indexOf(element.getRaw())

    override fun removeAll(elements: Collection<NBTData>) = sourceList.removeAll(elements.map { it.getRaw() })

    override fun lastIndexOf(element: NBTData): Int = sourceList.lastIndexOf(element.getRaw())

    override val size: Int get() = sourceList.size

    override fun clear() = sourceList.clear()

    override fun containsAll(elements: Collection<NBTData>) = sourceList.containsAll(elements.map { it.getRaw() })

    override fun contains(element: NBTData) = sourceList.contains(element.getRaw())

    override fun addAll(elements: Collection<NBTData>) = sourceList.addAll(elements.map { it.getRaw() })

    override fun addAll(index: Int, elements: Collection<NBTData>) = sourceList.addAll(index, elements.map { it.getRaw() })

    override fun subList(fromIndex: Int, toIndex: Int) = sourceList.subList(fromIndex, toIndex).let { source ->
        object : AbstractMutableList<NBTData>() {
            override fun add(index: Int, element: NBTData) = source.add(index, element.getRaw())
            override val size: Int get() = source.size
            override fun get(index: Int) = NBTAdapter.adaptNms(source[index])
            override fun removeAt(index: Int) = NBTAdapter.adaptNms(source.removeAt(index))
            override fun set(index: Int, element: NBTData) = NBTAdapter.adaptNms(source.set(index, element.getRaw()))
        }
    }

    override fun iterator() = listIterator()

    override fun listIterator() = listIterator(0)

    override fun listIterator(index: Int) = sourceList.listIterator().let { source ->
        object : MutableListIterator<NBTData> {
            override fun add(element: NBTData) = source.add(element.getRaw())
            override fun hasNext() = source.hasNext()
            override fun hasPrevious() = source.hasPrevious()
            override fun next() = NBTAdapter.adaptNms(source.next())
            override fun nextIndex() = source.nextIndex()
            override fun previous() = NBTAdapter.adaptNms(source.previous())
            override fun previousIndex() = source.previousIndex()
            override fun remove() = source.remove()
            override fun set(element: NBTData) = source.set(element.getRaw())
        }
    }

    override fun retainAll(elements: Collection<NBTData>) = sourceList.retainAll(elements.map { it.getRaw() })

}
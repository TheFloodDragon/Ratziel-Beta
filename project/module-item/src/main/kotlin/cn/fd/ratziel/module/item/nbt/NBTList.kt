package cn.fd.ratziel.module.item.nbt

/**
 * NBTList
 *
 * @author TheFloodDragon
 * @since 2024/3/15 20:31
 */
@Suppress("UNCHECKED_CAST")
class NBTList(rawData: Any) : NBTData(rawData, NBTType.LIST), MutableList<NBTData> {

    constructor() : this(new())

    constructor(list: Iterable<*>) : this(NBTAdapter.adaptList(list).getData())

    /**
     * [java.util.List] is the same as [MutableList] in [kotlin.collections]
     * Because [MutableList] will be compiled to [java.util.List]
     */
    internal val sourceList get() = NMSUtil.NtList.sourceField.get(data) as MutableList<Any>

    override val content: List<NBTData> get() = sourceList.map { NBTAdapter.adaptNms(it) }

    /**
     * 获取数据
     */
    override operator fun get(index: Int): NBTData = sourceList[index].let { NBTAdapter.adaptNms(it) }

    /**
     * 在索引处添加数据
     */
    override fun add(index: Int, element: NBTData) = sourceList.add(index, element.getData())

    /**
     * 在末尾添加数据
     */
    override fun add(element: NBTData) = sourceList.add(element.getData())

    /**
     * 根据索引删除数据
     */
    override fun removeAt(index: Int): NBTData = sourceList.removeAt(index).let { NBTAdapter.adaptNms(it) }

    /**
     * 删除某个指定数据
     */
    override fun remove(element: NBTData) = sourceList.remove(element.getData())

    /**
     * 设置索引处的数据
     */
    override fun set(index: Int, element: NBTData): NBTData = sourceList.set(index, element.getData()).let { NBTAdapter.adaptNms(it) }

    /**
     * 克隆数据
     */
    fun clone() = this.apply { data = NMSUtil.NtList.methodClone.invoke(data)!! }

    companion object {

        fun new() = new(ArrayList())

        fun new(list: List<Any>) = NMSUtil.NtList.constructor.instance(list, 0.toByte())!!

    }

    /**
     * 列表是否为空
     */
    override fun isEmpty(): Boolean = sourceList.isEmpty()

    override fun indexOf(element: NBTData): Int = sourceList.indexOf(element.getData())

    override fun removeAll(elements: Collection<NBTData>) = sourceList.removeAll(elements.map { it.getData() })

    override fun lastIndexOf(element: NBTData): Int = sourceList.lastIndexOf(element.getData())

    override val size: Int get() = sourceList.size

    override fun clear() = sourceList.clear()

    override fun containsAll(elements: Collection<NBTData>) = sourceList.containsAll(elements.map { it.getData() })

    override fun contains(element: NBTData) = sourceList.contains(element.getData())

    override fun addAll(elements: Collection<NBTData>) = sourceList.addAll(elements.map { it.getData() })

    override fun addAll(index: Int, elements: Collection<NBTData>) = sourceList.addAll(index, elements.map { it.getData() })

    override fun subList(fromIndex: Int, toIndex: Int) = sourceList.subList(fromIndex, toIndex).let { source ->
        object : AbstractMutableList<NBTData>() {
            override fun add(index: Int, element: NBTData) = source.add(index, element.getData())
            override val size: Int get() = source.size
            override fun get(index: Int) = NBTAdapter.adaptNms(source[index])
            override fun removeAt(index: Int) = NBTAdapter.adaptNms(source.removeAt(index))
            override fun set(index: Int, element: NBTData) = NBTAdapter.adaptNms(source.set(index, element.getData()))
        }
    }

    override fun iterator() = listIterator()

    override fun listIterator() = listIterator(0)

    override fun listIterator(index: Int) = sourceList.listIterator().let { source ->
        object : MutableListIterator<NBTData> {
            override fun add(element: NBTData) = source.add(element.getData())
            override fun hasNext() = source.hasNext()
            override fun hasPrevious() = source.hasPrevious()
            override fun next() = NBTAdapter.adaptNms(source.next())
            override fun nextIndex() = source.nextIndex()
            override fun previous() = NBTAdapter.adaptNms(source.previous())
            override fun previousIndex() = source.previousIndex()
            override fun remove() = source.remove()
            override fun set(element: NBTData) = source.set(element.getData())
        }
    }

    override fun retainAll(elements: Collection<NBTData>) = sourceList.retainAll(elements.map { it.getData() })

}
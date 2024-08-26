package cn.fd.ratziel.module.nbt

/**
 * NBTList
 *
 * @author TheFloodDragon
 * @since 2024/3/15 20:31
 */
open class NBTList(
    /**
     * 源数据 - 采用 Java原生数据结构
     * [NBTList] 的所有操作都是在 [sourceList] 上操作的
     */
    val sourceList: MutableList<Any>
) : NBTData(NBTType.LIST), MutableList<NBTData> {

    constructor() : this(mutableListOf())

    /**
     * 数据内容
     */
    override val content: List<NBTData> get() = this

    /**
     * 存储元素的类型
     */
    val elementType get() = if (content.isNotEmpty()) content.first().type else NBTType.END

    /**
     * 获取数据
     */
    override operator fun get(index: Int) = sourceList[index].let { NBTAdapter.box(it) }

    /**
     * 设置索引处的数据
     */
    override fun set(index: Int, element: NBTData): NBTData = sourceList.set(index, element.content).let { NBTAdapter.box(it) }

    /**
     * 在索引处添加数据
     */
    override fun add(index: Int, element: NBTData) = sourceList.add(index, element.content)

    /**
     * 在末尾添加数据
     */
    override fun add(element: NBTData) = sourceList.add(element.content)

    /**
     * 在末尾添加多个数据
     */
    override fun addAll(elements: Collection<NBTData>) = sourceList.addAll(elements.map { it.content })

    /**
     * 在索引处添加多个数据
     */
    override fun addAll(index: Int, elements: Collection<NBTData>) = sourceList.addAll(index, elements.map { it.content })

    /**
     * 根据索引删除数据
     */
    override fun removeAt(index: Int): NBTData = sourceList.removeAt(index).let { NBTAdapter.box(it) }

    /**
     * 删除某个指定数据
     */
    override fun remove(element: NBTData) = sourceList.remove(element.content)

    /**
     * 删除多个数据
     */
    override fun removeAll(elements: Collection<NBTData>) = sourceList.removeAll(elements.map { it.content })

    /**
     * 克隆数据
     */
    override fun clone() = NBTList().apply { this.forEach { add(it.clone()) } }

    companion object {

        @JvmStatic
        fun of(list: Iterable<Any>): NBTList = NBTList(list.toMutableList())

        @JvmStatic
        fun of(list: Iterable<NBTData>): NBTList = NBTList().apply { addAll(list) }

    }

    override fun clear() = sourceList.clear()

    override val size get() = sourceList.size

    override fun isEmpty() = sourceList.isEmpty()

    override fun contains(element: NBTData) = sourceList.contains(element.content)

    override fun containsAll(elements: Collection<NBTData>) = sourceList.containsAll(elements.map { it.content })

    override fun indexOf(element: NBTData) = sourceList.indexOf(element.content)

    override fun lastIndexOf(element: NBTData) = sourceList.lastIndexOf(element.content)

    override fun subList(fromIndex: Int, toIndex: Int) = object : AbstractMutableList<NBTData>() {
        val ref = sourceList.subList(fromIndex, toIndex)
        override fun add(index: Int, element: NBTData) = ref.add(index, element.content)
        override val size: Int get() = ref.size
        override fun get(index: Int) = NBTAdapter.box(ref[index])
        override fun removeAt(index: Int) = NBTAdapter.box(ref.removeAt(index))
        override fun set(index: Int, element: NBTData) = NBTAdapter.box(ref.set(index, element.content))
    }

    override fun iterator() = listIterator()

    override fun listIterator() = listIterator(0)

    override fun listIterator(index: Int) = object : MutableListIterator<NBTData> {
        val ref = sourceList.listIterator()
        override fun add(element: NBTData) = ref.add(element.content)
        override fun hasNext() = ref.hasNext()
        override fun hasPrevious() = ref.hasPrevious()
        override fun next() = NBTAdapter.box(ref.next())
        override fun nextIndex() = ref.nextIndex()
        override fun previous() = NBTAdapter.box(ref.previous())
        override fun previousIndex() = ref.previousIndex()
        override fun remove() = ref.remove()
        override fun set(element: NBTData) = ref.set(element.content)
    }

    override fun retainAll(elements: Collection<NBTData>) = sourceList.retainAll(elements.map { it.content })

}
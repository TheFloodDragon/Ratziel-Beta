package cn.fd.ratziel.module.nbt

/**
 * NBTList
 *
 * @author TheFloodDragon
 * @since 2024/3/15 20:31
 */
open class NBTList(
    /**
     * 源数据
     */
    val sourceList: MutableList<NBTData>
) : NBTData(NBTType.LIST), MutableList<NBTData> by sourceList {

    constructor() : this(mutableListOf())

    /**
     * 数据内容
     */
    override val content: List<NBTData> get() = sourceList

    /**
     * 存储元素的类型
     */
    val elementType get() = if (content.isNotEmpty()) content.first().type else NBTType.END

    /**
     * 克隆数据
     */
    override fun clone() = NBTList().apply { this.forEach { add(it.clone()) } }

    companion object {

        @JvmStatic
        fun of(list: Iterable<NBTData>): NBTList = NBTList().apply { addAll(list) }

    }

}
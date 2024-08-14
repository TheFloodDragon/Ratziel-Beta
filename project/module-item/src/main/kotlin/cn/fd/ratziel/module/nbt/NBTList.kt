package cn.fd.ratziel.module.nbt

/**
 * NBTList
 *
 * @author TheFloodDragon
 * @since 2024/3/15 20:31
 */
open class NBTList(override val content: MutableList<NBTData>) : NBTData(NBTType.LIST), MutableList<NBTData> by content {

    constructor() : this(mutableListOf())

    constructor(iterable: Iterable<NBTData>) : this(iterable.toMutableList())

    constructor(array: Array<NBTData>) : this(array.toMutableList())

    /**
     * 克隆数据
     */
    override fun clone() = NBTList(this.content.map { it.clone() })

}
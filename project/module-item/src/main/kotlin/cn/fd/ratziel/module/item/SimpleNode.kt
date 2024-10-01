package cn.fd.ratziel.module.item

import cn.fd.ratziel.module.item.api.ItemNode

/**
 * SimpleNode - [ItemNode] 实现
 *
 * @author TheFloodDragon
 * @since 2024/3/16 11:53
 */
data class SimpleNode(
    override val name: String,
    override val parent: ItemNode
) : ItemNode {

    constructor(name: String) : this(name, ItemNode.ROOT)

}
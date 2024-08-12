package cn.fd.ratziel.module.item.impl

import cn.fd.ratziel.module.item.api.ItemNode

/**
 * OccupyNode - [ItemNode] 实现
 *
 * @author TheFloodDragon
 * @since 2024/3/16 11:53
 */
data class OccupyNode(
    override val name: String,
    override val parent: ItemNode
) : ItemNode {

    constructor(name: String) : this(name, ItemNode.ROOT)

}
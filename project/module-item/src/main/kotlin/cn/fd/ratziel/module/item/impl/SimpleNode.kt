package cn.fd.ratziel.module.item.impl

import cn.fd.ratziel.module.item.api.ItemNode

/**
 * SimpleNode - [cn.fd.ratziel.module.item.api.ItemNode] 实现
 *
 * @author TheFloodDragon
 * @since 2024/3/16 11:53
 */
@Deprecated("This class is deprecated", ReplaceWith("NbtPath.Node"))
data class SimpleNode(
    override val name: String,
    override val parent: ItemNode
) : ItemNode {

    constructor(name: String) : this(name, ItemNode.Companion.ROOT)

}
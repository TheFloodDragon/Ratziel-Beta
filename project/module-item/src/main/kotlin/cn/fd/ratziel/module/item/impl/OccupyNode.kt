package cn.fd.ratziel.module.item.impl

import cn.fd.ratziel.module.item.api.ItemNode
import cn.fd.ratziel.module.item.nms.ItemSheet

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

    companion object {

        /**
         * 默认节点 - [CUSTOM_DATA_NODE]
         */
        val CUSTOM_DATA_NODE = OccupyNode(ItemSheet.CUSTOM_DATA)

    }

}
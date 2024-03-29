package cn.fd.ratziel.module.item.api.common

import cn.fd.ratziel.module.item.api.NodeDistributor

/**
 * OccupyNode - [NodeDistributor] 实现
 *
 * @author TheFloodDragon
 * @since 2024/3/16 11:53
 */
data class OccupyNode(
    override val name: String,
    override val parent: NodeDistributor?
) : NodeDistributor {

    companion object {

        /**
         * 一些默认节点
         */
        val APEX_NODE = OccupyNode("!", null)

    }

}
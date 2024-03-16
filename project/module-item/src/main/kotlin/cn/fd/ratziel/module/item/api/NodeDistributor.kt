package cn.fd.ratziel.module.item.api

/**
 * NodeDistributor - 物品节点分配器
 *
 * @author TheFloodDragon
 * @since 2024/3/16 10:43
 */
interface NodeDistributor {

    /**
     * 节点名称
     */
    val name: String

    /**
     * 上级节点分配器 (空代表最高级)
     */
    val parent: NodeDistributor?

}
package cn.fd.ratziel.module.item.api

/**
 * ItemNode - 物品节点
 *
 * @author TheFloodDragon
 * @since 2024/3/16 10:43
 */
interface ItemNode {

    /**
     * 节点名称
     */
    val name: String

    /**
     * 上级节点 (空代表最高级)
     */
    val parent: ItemNode?

}
package cn.fd.ratziel.module.itemengine.api.attribute.core

/**
 * AttributeNode - 属性的节点
 *
 * @author TheFloodDragon
 * @since 2023/12/2 23:18
 */
interface AttributeNode : AttributeBase {
    /**
     * 属性节点
     */
    val node: String
}
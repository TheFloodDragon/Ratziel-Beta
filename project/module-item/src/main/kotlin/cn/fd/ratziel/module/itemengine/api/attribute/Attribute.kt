package cn.fd.ratziel.module.itemengine.api.attribute

/**
 * Attribute - 属性
 *
 * @author TheFloodDragon
 * @since 2023/11/25 16:00
 */
interface Attribute<T> {

    /**
     * 属性节点
     */
    val node: String

    /**
     * 属性值
     */
    val value: T

}
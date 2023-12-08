package cn.fd.ratziel.module.itemengine.api.attribute.core

/**
 * AttributeValue - 属性的值
 *
 * @author TheFloodDragon
 * @since 2023/12/2 23:18
 */
interface AttributeValue<T> : AttributeBase {
    /**
     * 属性值
     */
    val value: T
}
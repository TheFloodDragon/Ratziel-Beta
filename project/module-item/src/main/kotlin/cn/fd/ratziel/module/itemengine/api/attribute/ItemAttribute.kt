package cn.fd.ratziel.module.itemengine.api.attribute

import cn.fd.ratziel.module.itemengine.api.attribute.core.Attribute
import kotlinx.serialization.Serializable

/**
 * ItemAttribute - 物品属性
 *
 * @author TheFloodDragon
 * @since 2023/12/8 21:43
 */
@Serializable
abstract class ItemAttribute<T>(
    private val unchangeableNode: String,
) : Attribute<T>, NBTTransformer {

    /**
     * 重新并只有 GET 方法, 使其不会被序列化
     */
    override val node: String get() = unchangeableNode
    override val value: T get() = getInstance()

    /**
     * 获取属性目标实例
     */
    @Suppress("UNCHECKED_CAST")
    open fun getInstance(): T = this as T

}
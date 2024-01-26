package cn.fd.ratziel.module.itemengine.api.attribute

import cn.fd.ratziel.module.itemengine.nbt.NBTTag

/**
 * ItemAttribute - 物品属性
 *
 * @author TheFloodDragon
 * @since 2023/12/8 21:43
 */
interface ItemAttribute<T> : Attribute<T>, NBTTransformer {

    /**
     * 重新并只有 GET 方法, 使其不会被序列化
     */
    override val node: String get() = node()
    override val value: T get() = instance()

    /**
     * 获取节点 (默认为顶级节点)
     */
    fun node(): String = NBTTag.APEX_NODE_SIGN

    /**
     * 获取属性目标实例 (默认为继承类)
     */
    @Suppress("UNCHECKED_CAST")
    fun instance(): T = this as T

}
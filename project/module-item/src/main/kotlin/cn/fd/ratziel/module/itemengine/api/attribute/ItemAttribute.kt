package cn.fd.ratziel.module.itemengine.api.attribute

import cn.fd.ratziel.module.itemengine.nbt.NBTTag
import kotlinx.serialization.Transient

/**
 * ItemAttribute - 物品属性
 *
 * @author TheFloodDragon
 * @since 2023/12/8 21:43
 */
interface ItemAttribute<T> : Attribute<T> {

    /**
     * 获取节点 (默认为顶级节点)
     */
    @Transient
    override val node: String get() = NBTTag.APEX_NODE_SIGN

    /**
     * 获取属性目标实例 (默认为继承类)
     */
    @Suppress("UNCHECKED_CAST")
    @Transient
    override val value: T get() = this as T

    /**
     * 属性转化器
     * 注意: 若要重写请只设置GET方法, 以防止被序列化
     */
    @Transient
    val transformer: NBTTransformer<T>

}
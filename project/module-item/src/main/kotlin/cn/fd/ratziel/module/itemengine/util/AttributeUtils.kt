package cn.fd.ratziel.module.itemengine.util

import cn.fd.ratziel.module.itemengine.api.attribute.ItemAttribute
import cn.fd.ratziel.module.itemengine.nbt.NBTTag

/**
 * 将NBT标签应用到物品属性上
 * @param from 来自于的NBT的标签
 */
fun <T> ItemAttribute<T>.applyFrom(from: NBTTag) =
    this.apply { from.editShallow(node) { tag -> this.transformer.transform(value, tag) } }

/**
 * 将物品属性应用于NBT标签上
 * @param to 应用到的NBT标签
 */
fun <T> ItemAttribute<T>.applyTo(to: NBTTag) =
    (to[this.node] as? NBTTag)?.let { this.transformer.detransform(value, it) }
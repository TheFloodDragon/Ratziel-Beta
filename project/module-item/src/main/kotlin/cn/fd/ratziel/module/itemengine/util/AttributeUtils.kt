package cn.fd.ratziel.module.itemengine.util

import cn.fd.ratziel.module.itemengine.api.attribute.ItemAttribute
import cn.fd.ratziel.module.itemengine.nbt.NBTTag

/**
 * 将一个物品属性转换成NBT标签并应用到源标签上
 */
fun <T> ItemAttribute<T>.transformTo(source: NBTTag) =
    this.also { source.editShallow(it.node) { tag -> this.transform(tag) } }
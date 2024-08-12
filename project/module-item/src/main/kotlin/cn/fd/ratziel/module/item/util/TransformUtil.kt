@file:Suppress("NOTHING_TO_INLINE")

package cn.fd.ratziel.module.item.util

import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.ItemNode
import cn.fd.ratziel.module.item.nbt.NBTCompound
import cn.fd.ratziel.module.item.nbt.NBTData
import cn.fd.ratziel.module.item.nbt.TagHelper
import java.util.function.Consumer
import java.util.function.Supplier

//@Deprecated("不适用")
//inline fun <T> ItemTransformer<T>.toApexData(component: T, data: ItemData.Mutable): ItemData.Mutable = ComponentUtil.toData(data, component, this)
//
//@Deprecated("不适用")
//inline fun <T> ItemTransformer<T>.toApexComponent(data: ItemData): T = ComponentUtil.toComponent(data, this)
//
//@Deprecated("不适用")
//inline fun ItemTransformer<*>.toApexDataUncheck(component: Any, data: ItemData.Mutable): ItemData.Mutable = ComponentUtil.toData(data, component, uncheck(this))
//
//@Deprecated("不适用")
//inline fun ItemTransformer<*>.toApexComponentUncheck(data: ItemData): Any = ComponentUtil.toComponent(data, uncheck(this))

/**
 * 读取并处理 [NBTCompound]
 * @see [TagHelper.readCreatable]
 */
inline fun ItemData.handle(tailNode: ItemNode, noinline action: NBTCompound.() -> Unit) = action(TagHelper.readCreatable(this.tag, tailNode))

/**
 * 深度读取指定类型的 [NBTData]
 * @see [TagHelper.read]
 */
inline fun <reified T : NBTData> ItemData.read(tailNode: ItemNode, action: Consumer<T>) =
    (TagHelper.read(this.tag, tailNode) as? T)?.let { action.accept(it) }

/**
 * 深度写入指定类型的 [NBTData]
 * @see [TagHelper.write]
 */
inline fun ItemData.write(tailNode: ItemNode, data: NBTData?) {
    if (data != null) TagHelper.write(this.tag, tailNode, data)
}

/**
 * 深度写入指定类型的 [NBTData]
 */
inline fun ItemData.write(tailNode: ItemNode, data: Supplier<out NBTData?>) = write(tailNode, data.get())
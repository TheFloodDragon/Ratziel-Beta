@file:Suppress("NOTHING_TO_INLINE")

package cn.fd.ratziel.module.item.util

import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.ItemNode
import cn.fd.ratziel.module.nbt.NBTCompound
import cn.fd.ratziel.module.nbt.NBTData
import cn.fd.ratziel.module.nbt.NBTHelper
import java.util.function.Consumer
import java.util.function.Supplier

/**
 * 读取并处理 [NBTCompound]
 * @see [NBTHelper.readCreatable]
 */
inline fun ItemData.handle(tailNode: ItemNode, noinline action: NBTCompound.() -> Unit) = action(NBTHelper.readCreatable(this.tag, tailNode))

/**
 * 深度读取指定类型的 [NBTData]
 * @see [NBTHelper.read]
 */
inline fun <reified T : NBTData> ItemData.read(tailNode: ItemNode, action: Consumer<T>) = this.read<T>(tailNode)?.let { action.accept(it) }

/**
 * 深度读取指定类型的 [NBTData]
 * @see [NBTHelper.read]
 */
inline fun <reified T : NBTData> ItemData.read(tailNode: ItemNode) = (NBTHelper.read(this.tag, tailNode) as? T)

/**
 * 深度写入指定类型的 [NBTData]
 * @see [NBTHelper.write]
 */
inline fun ItemData.write(tailNode: ItemNode, data: NBTData?) {
    if (data != null) NBTHelper.write(this.tag, tailNode, data)
}

/**
 * 深度写入指定类型的 [NBTData]
 */
inline fun ItemData.write(tailNode: ItemNode, data: Supplier<out NBTData?>) = write(tailNode, data.get())
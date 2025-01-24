@file:Suppress("NOTHING_TO_INLINE")

package cn.fd.ratziel.module.item.util

import cn.altawk.nbt.tag.NbtCompound
import cn.altawk.nbt.tag.NbtTag
import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.ItemNode
import cn.fd.ratziel.module.nbt.NBTHelper
import java.util.function.Consumer
import java.util.function.Supplier


/**
 * 读取并处理 [NbtCompound]
 * @see [NBTHelper.readCreatable]
 */
inline fun ItemData.handle(tailNode: ItemNode, noinline action: NbtCompound.() -> Unit) = action(NBTHelper.readCreatable(this.tag, tailNode))

/**
 * 深度读取指定类型的 [NbtTag]
 * @see [NBTHelper.read]
 */
inline fun <reified T : NbtTag> ItemData.read(tailNode: ItemNode, action: Consumer<T>) = this.read<T>(tailNode)?.let { action.accept(it) }

/**
 * 深度读取指定类型的 [NbtTag]
 * @see [NBTHelper.read]
 */
inline fun <reified T : NbtTag> ItemData.read(tailNode: ItemNode) = (NBTHelper.read(this.tag, tailNode) as? T)

/**
 * 深度写入指定类型的 [NbtTag]
 * @see [NBTHelper.write]
 */
inline fun ItemData.write(tailNode: ItemNode, data: NbtTag?) {
    if (data != null) NBTHelper.write(this.tag, tailNode, data)
}

/**
 * 深度写入指定类型的 [NbtTag]
 */
inline fun ItemData.write(tailNode: ItemNode, data: Supplier<out NbtTag?>) = write(tailNode, data.get())
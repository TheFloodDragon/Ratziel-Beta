@file:Suppress("NOTHING_TO_INLINE")

package cn.fd.ratziel.module.item.util

import cn.fd.ratziel.function.util.uncheck
import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.ItemTransformer
import cn.fd.ratziel.module.item.impl.builder.ComponentUtil
import cn.fd.ratziel.module.item.nbt.NBTData
import java.util.function.Consumer

inline fun <T> ItemTransformer<T>.toApexData(component: T): ItemData = ComponentUtil.toData(component, this)

inline fun <T> ItemTransformer<T>.toApexComponent(data: ItemData): T = ComponentUtil.toComponent(data, this)

inline fun ItemTransformer<*>.toApexDataUncheck(component: Any): ItemData = ComponentUtil.toData(component, uncheck(this))

inline fun ItemTransformer<*>.toApexComponentUncheck(data: ItemData): Any = ComponentUtil.toComponent(data, uncheck(this))

/**
 * 转换[NBTData], 若成功转换(不为空), 则执行 [action]
 */
inline fun <reified T : NBTData> NBTData?.castThen(action: Consumer<T>) = (this as? T)?.let { action.accept(it) }

inline fun <reified T : NBTData> ItemData?.castThen(node: String, action: Consumer<T>) = this?.tag?.get(node)?.castThen<T>(action)
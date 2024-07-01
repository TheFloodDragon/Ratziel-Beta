@file:Suppress("NOTHING_TO_INLINE")

package cn.fd.ratziel.module.item.util

import cn.fd.ratziel.function.util.uncheck
import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.ItemTransformer

inline fun <T> ItemTransformer<T>.toApexData(component: T): ItemData = ComponentUtil.toData(component, this)

inline fun <T> ItemTransformer<T>.toApexComponent(data: ItemData): T = ComponentUtil.toComponent(data, this)

inline fun ItemTransformer<*>.toApexDataUncheck(component: Any): ItemData = ComponentUtil.toData(component, uncheck(this))

inline fun ItemTransformer<*>.toApexComponentUncheck(data: ItemData): Any = ComponentUtil.toComponent(data, uncheck(this))
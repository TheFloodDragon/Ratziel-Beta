package cn.fd.ratziel.module.item.api.service

import cn.fd.ratziel.Identifier
import java.util.function.BiConsumer
import java.util.function.Function

/**
 * ItemServiceFunction - 物品服务 获取|设置 函数
 *
 * @author TheFloodDragon
 * @since 2024/5/4 12:32
 */
open class ItemServiceFunction<T>(
    open val getter: ItemServiceGetter<T>,
    open val setter: ItemServiceSetter<T>
) : ItemServiceGetter<T> by getter, ItemServiceSetter<T> by setter

/**
 * ItemServiceGetter - 物品服务获取函数
 *
 * @author TheFloodDragon
 * @since 2024/5/4 11:05
 */
fun interface ItemServiceGetter<T> : Function<Identifier, T?>

/**
 * ItemServiceSetter - 物品服务设置函数
 *
 * @author TheFloodDragon
 * @since 2024/5/4 12:16
 */
fun interface ItemServiceSetter<T> : BiConsumer<Identifier, T>
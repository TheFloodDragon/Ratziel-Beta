package cn.fd.ratziel.module.item.api.component.transformer

import cn.fd.ratziel.module.item.api.component.ItemComponentType

/**
 * DelegateTransformer
 * 
 * @author TheFloodDragon
 * @since 2026/1/1 22:09
 */
open class DelegateTransformer<T>(
    val jsonTransformer: ItemComponentType.JsonTransformer<T>,
    val nbtTransformer: ItemComponentType.NbtTransformer<T>,
) : ItemComponentType.Transformer<T>,
    ItemComponentType.JsonTransformer<T> by jsonTransformer,
    ItemComponentType.NbtTransformer<T> by nbtTransformer {

    override fun toString() = "DelegateTransformer(jsonTransformer=$jsonTransformer, nbtTransformer=$nbtTransformer)"

}

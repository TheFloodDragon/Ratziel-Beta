package cn.fd.ratziel.module.item.impl.component.internal

import cn.fd.ratziel.module.item.api.component.ItemComponentType

/**
 * ComponentListTransformer
 * 
 * @author TheFloodDragon
 * @since 2025/12/31 00:39
 */
class ComponentListTransformer<T : Any>(
    val transformer: ItemComponentType.Transformer<T>,
) : ItemComponentType.Transformer<List<T>> {

    override fun transform(src: Any): List<T> {
        return (src as Iterable<*>).map { transformer.transform(it!!) }
    }

    override fun detransform(tar: List<T>): Any {
        return tar.map { transformer.detransform(it) }
    }

}
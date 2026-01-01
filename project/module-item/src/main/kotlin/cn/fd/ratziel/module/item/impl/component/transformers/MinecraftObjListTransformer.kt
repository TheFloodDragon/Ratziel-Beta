package cn.fd.ratziel.module.item.impl.component.transformers

import cn.fd.ratziel.module.item.api.component.transformer.MinecraftComponentTransformer

/**
 * MinecraftObjListTransformer
 *
 * @author TheFloodDragon
 * @since 2025/12/31 00:39
 */
class MinecraftObjListTransformer<T : Any>(
    val transformer: MinecraftComponentTransformer<T>,
) : MinecraftComponentTransformer<List<T>> {

    override fun transformToMinecraftObj(tar: List<T>): Any {
        return tar.map { transformer.transformToMinecraftObj(it) }
    }

    override fun detransformFromMinecraftObj(src: Any): List<T> {
        return (src as Iterable<*>).map { transformer.detransformFromMinecraftObj(it!!) }
    }

}
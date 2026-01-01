package cn.fd.ratziel.module.item.impl.component.transformers

import cn.fd.ratziel.module.item.api.component.transformer.MinecraftComponentTransformer

/**
 * MinecraftObjNoTransformation
 * 
 * @author TheFloodDragon
 * @since 2026/1/5 20:26
 */
class MinecraftObjNoTransformation<T : Any> : MinecraftComponentTransformer<T> {

    override fun transformToMinecraftObj(tar: T): Any = tar

    @Suppress("UNCHECKED_CAST")
    override fun detransformFromMinecraftObj(src: Any): T = src as T

}
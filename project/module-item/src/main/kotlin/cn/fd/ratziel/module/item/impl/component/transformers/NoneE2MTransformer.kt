package cn.fd.ratziel.module.item.impl.component.transformers

import cn.fd.ratziel.module.item.impl.component.MinecraftE2MTransformer

/**
 * NoneE2MTransformer
 * 
 * @author TheFloodDragon
 * @since 2026/3/21 23:51
 */
@Suppress("UNCHECKED_CAST")
class NoneE2MTransformer<T: Any>: MinecraftE2MTransformer<T> {
    override fun toMinecraftObj(encapsulated: T): Any  = encapsulated
    override fun fromMinecraftObj(minecraftObj: Any): T  = minecraftObj as T
}
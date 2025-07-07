package cn.fd.ratziel.module.item

import cn.fd.ratziel.core.Identifier
import cn.fd.ratziel.core.functional.CacheContext
import cn.fd.ratziel.module.item.api.builder.ItemGenerator
import cn.fd.ratziel.module.item.impl.builder.DefaultGenerator
import java.util.concurrent.ConcurrentHashMap

/**
 * ItemManager
 *
 * @author TheFloodDragon
 * @since 2024/2/1 10:41
 */
object ItemManager {

    /**
     * 物品注册表
     */
    val registry: MutableMap<String, ItemGenerator> = ConcurrentHashMap()

    /**
     * 获取 [ItemGenerator] 的
     */
    fun getCacheContext(identifier: Identifier): CacheContext {
        return (registry[identifier.content] as? DefaultGenerator)?.cacheContext ?: CacheContext()
    }

}
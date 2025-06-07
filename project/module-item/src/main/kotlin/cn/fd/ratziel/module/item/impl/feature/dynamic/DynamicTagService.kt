package cn.fd.ratziel.module.item.impl.feature.dynamic

import cn.fd.ratziel.module.item.api.builder.ItemTagResolver
import taboolib.common.platform.function.warning
import java.util.concurrent.CopyOnWriteArrayList

/**
 * DynamicTagService
 *
 * @author TheFloodDragon
 * @since 2025/5/31 17:03
 */
object DynamicTagService {

    /**
     * 支持动态解析的 [ItemTagResolver] 表
     */
    val supportedResolvers: MutableList<ItemTagResolver> = CopyOnWriteArrayList()

    /**
     * 寻找动态标签解析器
     */
    fun findResolver(name: String): ItemTagResolver? {
        val resolver = supportedResolvers.firstOrNull { it.alias.contains(name) }
        if (resolver == null) warning("ItemTagResolver for DynamicTagService '$name' not found.")
        return resolver
    }

    /**
     * 注册一个动态标签解析器
     */
    fun registerResolver(resolver: ItemTagResolver) {
        supportedResolvers.add(resolver)
    }

}
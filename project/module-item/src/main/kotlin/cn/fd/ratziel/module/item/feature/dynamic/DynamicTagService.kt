package cn.fd.ratziel.module.item.feature.dynamic

import cn.fd.ratziel.module.item.api.builder.ItemTagResolver
import cn.fd.ratziel.module.item.feature.virtual.NativeVirtualItemRenderer
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

    init {
        NativeVirtualItemRenderer.acceptors.add(DynamicTagAcceptor)
    }

    /**
     * 寻找动态标签解析器
     */
    fun findResolver(name: String): ItemTagResolver {
        return supportedResolvers.firstOrNull { it.alias.contains(name) } ?: error("Dynamic resolver named '$name' not found!")
    }

    /**
     * 注册一个动态标签解析器
     */
    fun registerResolver(resolver: ItemTagResolver) {
        supportedResolvers.add(resolver)
    }

}
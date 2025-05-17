package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.core.serialization.elementAlias
import cn.fd.ratziel.module.item.ItemRegistry
import cn.fd.ratziel.module.item.impl.builder.provided.EnhancedListResolver
import cn.fd.ratziel.module.item.impl.builder.provided.InheritResolver
import cn.fd.ratziel.module.item.impl.builder.provided.PapiResolver
import java.util.concurrent.CopyOnWriteArraySet

/**
 * DefaultResolver
 *
 * @author TheFloodDragon
 * @since 2025/5/3 18:32
 */
object DefaultResolver {

    /**
     * 允许访问的节点列表, 仅在 限制性解析 时使用
     */
    val accessibleNodes: MutableSet<String> by lazy {
        CopyOnWriteArraySet(ItemRegistry.registry.flatMap { it.serializer.descriptor.elementAlias })
    }

    /**
     * 注册默认解析器
     */
    fun registerDefaults() {
        // 继承解析
        ItemRegistry.registerResolver(InheritResolver)
        // Papi 解析
        ItemRegistry.registerResolver(PapiResolver, true)
        // 标签解析
        ItemRegistry.registerResolver(SectionResolver, true)
        /*
          内接增强列表解析
          这里解释下为什么要放在标签解析的后面:
          放在标签解析的后面, 则是因为有些标签解析器可能会返回带有换行的字符串,
          就比如 InheritResolver (SectionTagResolver),
          因为列表是不能边遍历边修改的, 所以只能采用换行字符的方式.
        */
        ItemRegistry.registerResolver(EnhancedListResolver, true)
    }

}
package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.core.serialization.elementAlias
import cn.fd.ratziel.core.serialization.json.JsonTree
import cn.fd.ratziel.module.item.ItemRegistry
import cn.fd.ratziel.module.item.api.builder.ItemInterceptor
import cn.fd.ratziel.module.item.api.builder.ItemStream
import cn.fd.ratziel.module.item.impl.builder.provided.EnhancedListResolver
import cn.fd.ratziel.module.item.impl.builder.provided.PapiResolver
import java.util.concurrent.CopyOnWriteArraySet

/**
 * DefaultResolver
 *
 * @author TheFloodDragon
 * @since 2025/5/3 18:32
 */
object DefaultResolver : ItemInterceptor {

    /**
     * 允许访问的节点列表, 仅在 限制性解析 时使用
     */
    val accessibleNodes: MutableSet<String> by lazy {
        CopyOnWriteArraySet(ItemRegistry.registry.flatMap { it.serializer.descriptor.elementAlias })
    }

    /*
     * 尽管解释器 (Resolver, Interceptor, Source) 在解释的过程中是并行的,
     * 但是启动协程的顺序就近乎决定了解释器获取同步锁的顺序,
     * 比如解析器 (Resolver) 的执行是一开始就尝试拿锁的, 故而但看解析器执行链, 会发现它们其实是串行的.
     * 其他的同理也不是完全的并行或者串行, 这种方式虽说有可能会带来错位解释 (不按照启动协程的顺序),
     * 但相比来说, 却极为简洁, 在大多情况下无异常.
     */

    override suspend fun intercept(stream: ItemStream) {
        stream.tree.withValue { tree ->
            val root = tree.root
            if (root is JsonTree.ObjectNode) {
                // 限制性解析: 过滤掉限制的节点
                val filtered = root.value.filter { it.key in accessibleNodes }
                    .let { JsonTree.ObjectNode(it, null) }

                // 标签解析
                val analyzed = TaggedSectionResolver.analyze(filtered)
                TaggedSectionResolver.resolveAnalyzed(analyzed, stream.context)

                JsonTree.unfold(filtered) {

                    // Papi 解析
                    PapiResolver.resolve(it, stream.context)

                    /*
                      内接增强列表解析
                      这里解释下为什么要放在标签解析的后面:
                      放在标签解析的后面, 则是因为有些标签解析器可能会返回带有换行的字符串,
                      就比如 InheritResolver (SectionTagResolver),
                      因为列表是不能边遍历边修改的, 所以只能采用换行字符的方式.
                    */
                    EnhancedListResolver.resolve(it, stream.context)

                }
            }

        }
    }

}
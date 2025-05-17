package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.core.serialization.elementAlias
import cn.fd.ratziel.core.serialization.json.JsonTree
import cn.fd.ratziel.module.item.ItemRegistry
import cn.fd.ratziel.module.item.api.builder.ItemResolver
import cn.fd.ratziel.module.item.impl.builder.provided.EnhancedListResolver
import cn.fd.ratziel.module.item.impl.builder.provided.InheritResolver
import cn.fd.ratziel.module.item.impl.builder.provided.PapiResolver
import java.util.concurrent.CopyOnWriteArraySet
import java.util.function.Consumer

/**
 * DefaultResolver
 *
 * @author TheFloodDragon
 * @since 2025/5/3 18:32
 */
object DefaultResolver : ItemResolver {

    /**
     * 允许访问的节点列表, 仅在 限制性解析 时使用
     */
    val accessibleNodes: MutableSet<String> by lazy {
        CopyOnWriteArraySet(ItemRegistry.registry.flatMap { it.serializer.descriptor.elementAlias })
    }

    override fun resolve(node: JsonTree.Node, context: ArgumentContext) {
        // 只接受根节点
        if (node.parent != null) return
        // 继承解析
        InheritResolver.resolve(node, context)
        // 限制性解析
        val limitedResolve = Consumer<JsonTree.Node> {
            // Papi 解析
            PapiResolver.resolve(it, context)
            // 标签解析
            SectionResolver.resolve(it, context)
            /*
              内接增强列表解析
              这里解释下为什么要放在标签解析的后面:
              放在标签解析的后面, 则是因为有些标签解析器可能会返回带有换行的字符串,
              就比如 InheritResolver (SectionTagResolver),
              因为列表是不能边遍历边修改的, 所以只能采用换行字符的方式.
            */
            EnhancedListResolver.resolve(it, context)
        }

        if (node is JsonTree.ObjectNode) {
            for (entry in node.value) {
                // 限制性解析: 过滤非运行的节点
                if (entry.key in accessibleNodes) {
                    JsonTree.unfold(entry.value, limitedResolve)
                }
            }
        } else JsonTree.unfold(node, limitedResolve)
    }

}
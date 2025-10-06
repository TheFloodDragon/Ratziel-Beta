package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.core.contextual.ArgumentContext
import cn.fd.ratziel.core.serialization.json.JsonTree
import cn.fd.ratziel.module.item.ItemRegistry
import cn.fd.ratziel.module.item.api.builder.*
import cn.fd.ratziel.module.item.impl.builder.provided.TaggedSectionResolver
import java.util.concurrent.CopyOnWriteArraySet

/**
 * DefaultResolver
 *
 * @author TheFloodDragon
 * @since 2025/5/3 18:32
 */
@ParallelInterpretation
object DefaultResolver : ItemInterpreter {

    override suspend fun preFlow(stream: ItemStream) {
        stream.tree.withValue { tree ->
            tree.root.unfold {
                for (resolver in ItemRegistry.sectionResolvers) {
                    // 准备阶段
                    resolver.prepare(it, stream.context)
                }
            }
        }
    }

    override suspend fun interpret(stream: ItemStream) {
        stream.tree.withValue { tree ->
            resolveTree(tree, stream.context, ItemRegistry.sectionResolvers)
        }
    }

    /**
     * 允许访问的节点列表, 仅在 限制性解析 时使用
     */
    val accessibleNodes: MutableSet<String> by lazy {
        CopyOnWriteArraySet(ItemRegistry.registry.flatMap { it.elementNodes })
    }

    /**
     * 解析带有单个标签的 [JsonTree]
     * @param resolver 标签解析器
     * @param tree [JsonTree]
     * @param context 上下文
     */
    @JvmStatic
    fun resolveBy(resolver: ItemSectionResolver, tree: JsonTree, context: ArgumentContext) {
        resolveTree(tree, context, listOf(resolver))
    }

    /**
     * 解析带有单个标签的 [JsonTree]
     * @param resolver 标签解析器
     * @param tree [JsonTree]
     * @param context 上下文
     */
    @JvmStatic
    fun resolveBy(resolver: ItemTagResolver, tree: JsonTree, context: ArgumentContext) {
        this.resolveBy(TaggedSectionResolver(listOf(resolver)), tree, context)
    }

    /**
     * 解析 [JsonTree]
     *
     * @param tree [JsonTree]
     * @param context 上下文
     * @param resolvers 使用到的 [ItemSectionResolver] 列表
     */
    @JvmStatic
    fun resolveTree(tree: JsonTree, context: ArgumentContext, resolvers: List<ItemSectionResolver>) {
        makeFiltered(tree.root).unfold {
            for (resolver in resolvers) {
                // 解析节点 (包括所有类型的节点)
                resolver.resolve(it, context)
            }
        }
    }

    /**
     * 限制性解析: 仅保留允许访问的节点
     * @param root 根节点
     * @return 过滤后的根节点 [JsonTree.Node]
     */
    @JvmStatic
    fun makeFiltered(root: JsonTree.Node): JsonTree.Node {
        return if (root is JsonTree.ObjectNode) {
            // 限制性解析: 过滤掉限制的节点
            root.value.filter { it.key in accessibleNodes }
                .let { JsonTree.ObjectNode(it, null) }
        } else root
    }

}
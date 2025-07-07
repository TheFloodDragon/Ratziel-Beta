package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.core.functional.ArgumentContext
import cn.fd.ratziel.core.serialization.elementAlias
import cn.fd.ratziel.core.serialization.json.JsonTree
import cn.fd.ratziel.module.item.ItemRegistry
import cn.fd.ratziel.module.item.api.builder.ItemInterpreter
import cn.fd.ratziel.module.item.api.builder.ItemSectionResolver
import cn.fd.ratziel.module.item.api.builder.ItemStream
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CopyOnWriteArraySet

/**
 * DefaultResolver
 *
 * @author TheFloodDragon
 * @since 2025/5/3 18:32
 */
object DefaultResolver : ItemInterpreter.PreInterpretable {

    override suspend fun preFlow(stream: ItemStream) {
        stream.tree.withValue { tree ->
            tree.root.unfold {
                for (resolver in sectionResolvers) {
                    // 准备阶段
                    resolver.prepare(it, stream.context)
                }
            }
        }
    }

    override suspend fun interpret(stream: ItemStream) {
        stream.tree.withValue { tree ->
            resolveTree(tree, stream.context, sectionResolvers)
        }
    }

    /**
     * 允许访问的节点列表, 仅在 限制性解析 时使用
     */
    val accessibleNodes: MutableSet<String> by lazy {
        CopyOnWriteArraySet(ItemRegistry.registry.flatMap { it.serializer.descriptor.elementAlias })
    }

    /**
     * 默认 [ItemSectionResolver] 列表
     */
    val sectionResolvers: MutableList<ItemSectionResolver> = CopyOnWriteArrayList()

    /**
     * 注册默认的 [ItemSectionResolver]
     */
    @JvmStatic
    fun registerSectionResolver(resolver: ItemSectionResolver) {
        sectionResolvers.add(resolver)
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
package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.core.serialization.elementAlias
import cn.fd.ratziel.core.serialization.json.JsonTree
import cn.fd.ratziel.module.item.ItemRegistry
import cn.fd.ratziel.module.item.api.builder.ItemResolver
import cn.fd.ratziel.module.item.api.builder.ItemSectionResolver
import cn.fd.ratziel.module.item.impl.builder.provided.EnhancedListResolver
import cn.fd.ratziel.module.item.impl.builder.provided.PapiResolver
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import java.util.concurrent.CopyOnWriteArrayList

/**
 * DefaultResolver
 *
 * @author TheFloodDragon
 * @since 2025/5/3 18:32
 */
sealed class DefaultResolver : ItemResolver {

    /**
     * 允许访问的节点, 留空代表允许访问所有节点
     */
    protected open val accessibleNodes: List<String>? = null

    override fun resolve(element: JsonElement, context: ArgumentContext): JsonElement {
        // 过滤非允许的节点
        val nodes = accessibleNodes
        val filtered = if (nodes != null && element is JsonObject) {
            JsonObject(element.filter { nodes.contains(it.key) })
        } else element
        // 解析树
        return this.resolve(JsonTree(filtered), context)
    }

    fun resolve(tree: JsonTree, context: ArgumentContext): JsonElement {
        JsonTree.unfold(tree.root) {
            for (resolver in sectionResolvers) resolver.resolve(it, context)
        }
        return tree.toElement()
    }

    companion object : DefaultResolver() {

        /**
         * 仅允许参与序列化过程的节点
         */
        override val accessibleNodes: List<String> = ItemRegistry.registry.flatMap { it.serializer.descriptor.elementAlias }

        /**
         * 片段解析器列表
         */
        val sectionResolvers: MutableList<ItemSectionResolver> = mutableListOf(
            EnhancedListResolver,
            PapiResolver,
            SectionResolver,
        )

        /**
         * 标签解析器列表
         */
        val tagResolvers: MutableList<SectionTagResolver> = CopyOnWriteArrayList()

        /**
         * 匹配 [SectionTagResolver]
         */
        fun match(name: String): SectionTagResolver? = tagResolvers.find { it.names.contains(name) }

    }

}
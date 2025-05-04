package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.core.function.SimpleContext
import cn.fd.ratziel.core.serialization.elementAlias
import cn.fd.ratziel.core.serialization.json.JsonTree
import cn.fd.ratziel.module.item.ItemRegistry
import cn.fd.ratziel.module.item.api.builder.ItemResolver
import cn.fd.ratziel.module.item.api.builder.ItemSectionResolver
import cn.fd.ratziel.module.item.impl.builder.provided.EnhancedListResolver
import cn.fd.ratziel.module.item.impl.builder.provided.InheritResolver
import cn.fd.ratziel.module.item.impl.builder.provided.PapiResolver
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CopyOnWriteArrayList

/**
 * DefaultResolver
 *
 * @author TheFloodDragon
 * @since 2025/5/3 18:32
 */
open class DefaultResolver : ItemResolver {

    /**
     * 允许访问的节点, 留空代表允许访问所有节点
     */
    var accessibleNodes: List<String>? = null

    override fun resolve(element: JsonElement, context: ArgumentContext): JsonElement {
        // 过滤非允许的节点
        val nodes = accessibleNodes
        val filtered = if (nodes != null && element is JsonObject) {
            JsonObject(element.filter { nodes.contains(it.key) })
        } else element
        // 解析树
        val tree = JsonTree(filtered)
        this.resolveTree(tree, context) // 对树进行编辑解析
        return tree.toElement()
    }

    fun resolveTree(tree: JsonTree, context: ArgumentContext) {
        JsonTree.unfold(tree.root) {
            for (resolver in sectionResolvers) resolver.resolve(it, context)
        }
    }

    /**
     * 过滤非允许的节点, 生成 [JsonElement]
     */
    fun generateFiltered(element: JsonElement): JsonElement {
        val nodes = accessibleNodes
        val filtered = if (nodes != null && element is JsonObject) {
            JsonObject(element.filter { nodes.contains(it.key) })
        } else element
        return filtered
    }

    companion object Default : DefaultResolver() {

        init {
            // 仅允许参与序列化过程的节点
            this.accessibleNodes = ItemRegistry.registry.flatMap { it.serializer.descriptor.elementAlias }
        }

        /**
         * 片段解析器列表
         */
        val sectionResolvers: MutableList<ItemSectionResolver> = mutableListOf(
            EnhancedListResolver,
            InheritResolver,
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

    /**
     * ResolvationCache - 可复用的解析缓存机制
     */
    class ResolvationCache(
        val element: JsonElement,
        val resolver: DefaultResolver = Default,
        /**
         * 是否立刻解析一次 (异步)
         * 同时在获取结果 [fetchResult] 后, 会再次开启解析任务, 以便下一次调用
         */
        val resolveImmediately: Boolean = true,
    ) {

        private lateinit var future: CompletableFuture<JsonTree>
        private var initialized = false

        init {
            if (resolveImmediately) {
                resolveAsync()
            }
        }

        /**
         * 获取最终结果
         */
        fun fetchResult(): JsonElement {
            // 变量未初始化时无参数初始化
            if (!::future.isInitialized) {
                resolveAsync()
            }
            // 等待完成, 获取树
            val tree = future.get()
            initialized = false // 标记状态为未初始化
            // 若启用, 则再次开启解析任务
            if (resolveImmediately) resolveAsync()
            // 返回结果
            return tree.toElement()
        }

        /**
         * 异步解析
         */
        fun resolveAsync(context: ArgumentContext = SimpleContext()) {
            if (initialized) {
                future = future.thenApply {
                    // 已初始化了的再次解析
                    resolver.resolveTree(it, context); it
                }
            } else {
                future = CompletableFuture.supplyAsync {
                    // 过滤
                    val filtered = resolver.generateFiltered(element)
                    // 生成树并解析
                    JsonTree(filtered).also {
                        resolver.resolveTree(it, context)
                    }
                }
                initialized = true // 标记状态为已初始化
            }
        }

    }

}
package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.core.function.SimpleContext
import cn.fd.ratziel.core.serialization.elementAlias
import cn.fd.ratziel.core.serialization.json.JsonTree
import cn.fd.ratziel.module.item.ItemRegistry
import cn.fd.ratziel.module.item.api.builder.ItemResolver
import cn.fd.ratziel.module.item.impl.builder.provided.EnhancedListResolver
import cn.fd.ratziel.module.item.impl.builder.provided.InheritResolver
import cn.fd.ratziel.module.item.impl.builder.provided.PapiResolver
import kotlinx.serialization.json.JsonElement
import java.util.concurrent.CompletableFuture
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
    val accessibleNodes: MutableSet<String> = CopyOnWriteArraySet(ItemRegistry.registry.flatMap { it.serializer.descriptor.elementAlias })

    override fun resolve(element: JsonElement, context: ArgumentContext): JsonElement {
        val tree = JsonTree(element)
        this.resolveTree(tree, context) // 对树进行编辑解析
        return tree.toElement()
    }

    fun resolveTree(tree: JsonTree, context: ArgumentContext) {
        val root = tree.root
        // 继承解析
        InheritResolver.resolve(root, context)
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

        if (root is JsonTree.ObjectNode) {
            for (entry in root.value) {
                // 限制性解析: 过滤非运行的节点
                if (entry.key in accessibleNodes) {
                    JsonTree.unfold(entry.value, limitedResolve)
                }
            }
        } else JsonTree.unfold(root, limitedResolve)
    }


    /**
     * ResolvationCache - 可复用的解析缓存机制
     */
    class ResolvationCache(
        val element: JsonElement,
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
                    resolveTree(it, context); it
                }
            } else {
                future = CompletableFuture.supplyAsync {
                    // 生成树并解析
                    JsonTree(element).also {
                        resolveTree(it, context)
                    }
                }
                initialized = true // 标记状态为已初始化
            }
        }

    }

}
package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.core.serialization.elementAlias
import cn.fd.ratziel.core.serialization.json.JsonTree
import cn.fd.ratziel.module.item.ItemRegistry
import cn.fd.ratziel.module.item.api.builder.ItemInterpreter
import cn.fd.ratziel.module.item.api.builder.ItemSectionResolver
import cn.fd.ratziel.module.item.api.builder.ItemStream
import cn.fd.ratziel.module.item.api.builder.ItemTagResolver
import cn.fd.ratziel.module.item.impl.builder.provided.EnhancedListResolver
import cn.fd.ratziel.module.item.impl.builder.provided.InlineScriptResolver
import cn.fd.ratziel.module.item.impl.builder.provided.PapiResolver
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CopyOnWriteArraySet

/**
 * DefaultResolver
 *
 * @author TheFloodDragon
 * @since 2025/5/3 18:32
 */
object DefaultResolver : ItemInterpreter {

    /**
     * 允许访问的节点列表, 仅在 限制性解析 时使用
     */
    val accessibleNodes: MutableSet<String> by lazy {
        CopyOnWriteArraySet(ItemRegistry.registry.flatMap { it.serializer.descriptor.elementAlias })
    }

    /**
     * 默认的标签解析器列表
     */
    val tagResolvers: MutableList<ItemTagResolver> = CopyOnWriteArrayList()

    /**
     * 注册默认的标签解析器
     */
    @JvmStatic
    fun registerResolver(resolver: ItemTagResolver) {
        tagResolvers.add(resolver)
    }

    /*
     * 尽管解释器 (Resolver, Interpreter, Source) 在解释的过程中是并行的,
     * 但是启动协程的顺序就近乎决定了解释器获取同步锁的顺序,
     * 比如解析器 (Resolver) 的执行是一开始就尝试拿锁的, 故而但看解析器执行链, 会发现它们其实是串行的.
     * 其他的同理也不是完全的并行或者串行, 这种方式虽说有可能会带来错位解释 (不按照启动协程的顺序),
     * 但相比来说, 却极为简洁, 在大多情况下无异常.
     */

    override suspend fun interpret(stream: ItemStream) {
        stream.tree.withValue { tree ->
            val resolvers = arrayOf(
                // 内联脚本解析
                InlineScriptResolver,
                // Papi 解析
                PapiResolver,
                // 标签解析
                TaggedSectionResolver(tagResolvers),
                /*
                  内接增强列表解析
                  这里解释下为什么要放在标签解析的后面:
                  放在标签解析的后面, 则是因为有些标签解析器可能会返回带有换行的字符串,
                  就比如 InheritResolver (SectionTagResolver),
                  因为列表是不能边遍历边修改的, 所以只能采用换行字符的方式.
                */
                EnhancedListResolver,
            )
            // 挨个解析
            for (resolver in resolvers) {
                resolveTreeWithSectionResolver(resolver, tree, stream.context)
            }
        }
    }

    /**
     * 使用 [ItemSectionResolver] 解析 [JsonTree]
     */
    @JvmStatic
    suspend fun resolveTreeWithSectionResolver(resolver: ItemSectionResolver, tree: JsonTree, context: ArgumentContext) = coroutineScope {
        makeFiltered(tree.root).unfold {
            // 先解析节点
            resolver.resolve(it, context)
            // 解析字符串
            // 单个节点只解析 Primitive 类型, 即字符串
            if (it !is JsonTree.PrimitiveNode) return@unfold
            // 启动协程处理字符串 (一般来说不会有太大问题)
            this@coroutineScope.launch {
                val value = it.value
                // 判断有效节点
                if (value.isString && value !is JsonNull) {
                    // 解析字符串
                    val result = resolver.resolve(value.content, context)
                    // 更新节点内容
                    it.value = JsonPrimitive(result)
                }
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
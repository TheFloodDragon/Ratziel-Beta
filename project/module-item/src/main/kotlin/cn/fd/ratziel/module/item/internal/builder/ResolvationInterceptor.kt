package cn.fd.ratziel.module.item.internal.builder

import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.core.serialization.json.JsonTree
import cn.fd.ratziel.module.item.api.builder.ItemInterceptor
import cn.fd.ratziel.module.item.api.builder.ItemResolver
import cn.fd.ratziel.module.item.api.builder.ItemStream
import cn.fd.ratziel.module.item.impl.builder.DefaultResolver

/**
 * ResolvationInterceptor
 *
 * @author TheFloodDragon
 * @since 2025/5/17 14:07
 */
open class ResolvationInterceptor(
    val resolver: ItemResolver,
) : ItemInterceptor {

    override suspend fun intercept(stream: ItemStream) {
        stream.tree.withValue { tree ->
            this.resolve(tree.root, stream.context)
        }
    }

    fun resolve(node: JsonTree.Node, context: ArgumentContext) {
        JsonTree.unfold(node) { resolver.resolve(it, context) }
    }

    class Limited(
        resolver: ItemResolver,
        val limitsGetter: () -> Set<String> = { DefaultResolver.accessibleNodes },
    ) : ResolvationInterceptor(resolver) {

        override suspend fun intercept(stream: ItemStream) {
            stream.tree.withValue { tree ->
                val root = tree.root
                if (root is JsonTree.ObjectNode) {
                    for (entry in root.value) {
                        // 限制性解析: 过滤掉限制的节点
                        if (entry.key in limitsGetter.invoke()) {
                            this.resolve(entry.value, stream.context)
                        }
                    }
                } else this.resolve(root, stream.context)
            }
        }

    }

}
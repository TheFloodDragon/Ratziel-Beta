package cn.fd.ratziel.module.item.internal.builder

import cn.fd.ratziel.core.serialization.json.JsonTree
import cn.fd.ratziel.module.item.api.builder.ItemInterceptor
import cn.fd.ratziel.module.item.api.builder.ItemResolver
import cn.fd.ratziel.module.item.api.builder.ItemStream
import kotlinx.coroutines.CoroutineScope

/**
 * ResolvationInterceptor
 *
 * @author TheFloodDragon
 * @since 2025/5/17 14:07
 */
class ResolvationInterceptor(
    val resolver: ItemResolver,
) : ItemInterceptor {

    override suspend fun intercept(scope: CoroutineScope, stream: ItemStream) {
        stream.tree.withValue { tree ->
            JsonTree.unfold(tree.root) {
                resolver.resolve(it, stream.context)
            }
        }
    }

}
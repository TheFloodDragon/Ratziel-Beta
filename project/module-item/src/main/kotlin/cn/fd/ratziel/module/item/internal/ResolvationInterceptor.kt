package cn.fd.ratziel.module.item.internal

import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.core.serialization.json.JsonTree
import cn.fd.ratziel.module.item.api.builder.ItemInterceptor
import cn.fd.ratziel.module.item.api.builder.ItemResolver
import cn.fd.ratziel.module.item.api.builder.ItemStream

/**
 * ResolvationInterceptor
 *
 * @author TheFloodDragon
 * @since 2025/5/17 14:07
 */
open class ResolvationInterceptor(
    val resolver: ItemResolver,
) : ItemInterceptor {

    override suspend fun intercept(stream: ItemStream, context: ArgumentContext) {
        stream.tree.withValue {
            JsonTree.Companion.unfold(it.root) {
                resolver.resolve(it, context)
            }
        }
    }

    class Limited(resolver: ItemResolver,limitsGetter:()->Set<String>) : ResolvationInterceptor() {

    }

}
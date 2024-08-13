package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.function.ArgumentContext
import cn.fd.ratziel.module.item.api.builder.ItemResolver
import kotlinx.serialization.json.JsonElement

/**
 * DefaultItemResolver
 *
 * @author TheFloodDragon
 * @since 2024/8/13 10:49
 */
object DefaultItemResolver : ItemResolver {

    override fun resolve(element: JsonElement, context: ArgumentContext): JsonElement {
        TODO("Not yet implemented")
    }

}
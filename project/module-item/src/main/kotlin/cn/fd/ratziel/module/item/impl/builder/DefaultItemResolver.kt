package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.module.item.api.builder.ItemResolver
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject

/**
 * DefaultItemResolver
 *
 * @author TheFloodDragon
 * @since 2024/4/20 10:03
 */
class DefaultItemResolver : ItemResolver {

    override fun resolve(target: JsonElement) = if (target is JsonObject)
        buildJsonObject {
            target.forEach {
                if (DefaultItemSerializer.occupiedNodes.contains(it.key)) put(it.key, it.value)
            }
        } else target

}
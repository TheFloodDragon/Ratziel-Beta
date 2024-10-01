package cn.fd.ratziel.module.item.builder

import cn.fd.ratziel.function.ArgumentContext
import cn.fd.ratziel.module.item.api.builder.ItemSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

/**
 * SectionTransforming
 *
 * @author TheFloodDragon
 * @since 2024/10/1 14:56
 */
class SectionTransforming<T : Any>(
    val serializer: KSerializer<T>,
    val json: Json
) : ItemSerializer<T>, KSerializer<T> by serializer {

    override fun serialize(component: T, context: ArgumentContext): JsonElement {
        return json.encodeToJsonElement(serializer, component)
    }

    override fun deserialize(element: JsonElement, context: ArgumentContext): T {
        return json.decodeFromJsonElement(serializer, DefaultSectionResolver.resolve(element, context))
    }

}
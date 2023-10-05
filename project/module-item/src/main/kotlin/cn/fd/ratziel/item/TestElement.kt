package cn.fd.ratziel.item

import cn.fd.ratziel.adventure.ComponentSerializer
import cn.fd.ratziel.common.element.registry.NewElement
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.element.api.ElementHandler
import cn.fd.ratziel.core.serialization.baseJson
import cn.fd.ratziel.core.serialization.serializers.EnhancedListSerializer
import cn.fd.ratziel.item.meta.VItemMeta
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual

/**
 * TestElement
 *
 * @author TheFloodDragon
 * @since 2023/10/3 13:16
 */
@NewElement(
    "meta",
    space = "test"
)
class TestElement : ElementHandler {

    val serializers by lazy {
        SerializersModule {
            contextual(EnhancedListSerializer(ComponentSerializer))
        }
    }

    val json by lazy {
        Json(baseJson) {
            serializersModule = serializers
        }
    }

    override fun handle(element: Element) = try {
        println(element.property)

        val meta = json.decodeFromJsonElement<VItemMeta>(element.property)

        println(meta.displayName)
        println(meta.lore)

    } catch (ex: Exception) {
        ex.printStackTrace()
    }

}
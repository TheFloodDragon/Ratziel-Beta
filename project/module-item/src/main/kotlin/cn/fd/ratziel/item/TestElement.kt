package cn.fd.ratziel.item

import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.element.NewElement
import cn.fd.ratziel.core.element.api.ElementHandler
import cn.fd.ratziel.core.serialization.serializers.UUIDSerializer
import cn.fd.ratziel.item.meta.VItemMeta
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import java.util.*

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

    override fun handle(element: Element) = try {
        println(element.property)

        println(Json.encodeToString(UUIDSerializer,UUID.randomUUID()))
        println(Json.decodeFromString(UUIDSerializer,Json.encodeToString(UUIDSerializer,UUID.randomUUID())))

        val meta = Json.decodeFromJsonElement<VItemMeta>(element.property)

        println(meta.itemFlags)
        println(meta.attributeModifiers)
        println(meta.enchants)


    } catch (ex: Exception) {
        ex.printStackTrace()
    }
}
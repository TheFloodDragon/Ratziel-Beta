package cn.fd.ratziel.module.itemengine

import cn.fd.ratziel.common.element.registry.NewElement
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.element.api.ElementHandler
import cn.fd.ratziel.core.serialization.baseJson
import cn.fd.ratziel.module.itemengine.item.builder.DefaultItemGenerator
import cn.fd.ratziel.module.itemengine.item.builder.DefaultItemSerializer
import kotlinx.serialization.json.Json

/**
 * ItemElement
 *
 * @author TheFloodDragon
 * @since 2023/10/14 18:55
 */
@NewElement(
    "meta",
    space = "test"
)
object ItemElement : ElementHandler {

    val json by lazy {
        Json(baseJson) {
            serializersModule = DefaultItemSerializer.defaultSerializersModule
        }
    }

    override fun handle(element: Element) = try {

        val serializer = DefaultItemSerializer(json)
        val generator = DefaultItemGenerator()

        val meta = serializer.deserializeFromJson(element.property)

        println(meta.display)
        println(meta.characteristic)
        println(meta.durability)
        println(meta.nbt)

        println("————————————————————————————————")

        val testMeta = generator.build(meta)

        println(testMeta)

        println("————————————————————————————————")

        println(serializer.usedNodes.toList().toString())

    } catch (ex: Exception) {
        ex.printStackTrace()
    }

}
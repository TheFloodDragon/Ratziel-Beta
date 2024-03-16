package cn.fd.ratziel.module.item

import cn.fd.ratziel.common.element.registry.NewElement
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.element.api.ExtElementHandler
import cn.fd.ratziel.core.serialization.baseJson
import cn.fd.ratziel.core.serialization.baseSerializers
import cn.fd.ratziel.module.item.impl.ItemManager
import cn.fd.ratziel.module.itemengine.item.builder.DefaultItemGenerator
import cn.fd.ratziel.module.itemengine.item.builder.DefaultItemSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.plus

/**
 * ItemElement
 *
 * @author TheFloodDragon
 * @since 2023/10/14 18:55
 */
@NewElement(
    name = "meta",
    space = "test"
)
object ItemElement : ExtElementHandler {

    val json by lazy {
        Json(baseJson) {
            serializersModule = baseSerializers.plus(DefaultItemSerializer.defaultSerializersModule)
        }
    }

    override fun handle(element: Element) = try {

        val serializer = DefaultItemSerializer(json)
        val generator = DefaultItemGenerator(element)

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

        println("————————————————————————————————")

        // 注册物品
        ItemManager.registry[element.name] = generator
    } catch (ex: Exception) {
        ex.printStackTrace()
    }

    override fun onStart() {
        // 清除注册的物品
        ItemManager.registry.clear()
    }

}
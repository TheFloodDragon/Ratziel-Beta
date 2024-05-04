package cn.fd.ratziel.module.item

import cn.fd.ratziel.common.element.registry.NewElement
import cn.fd.ratziel.common.event.WorkspaceLoadEvent
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.element.api.ElementHandler
import cn.fd.ratziel.core.serialization.baseJson
import cn.fd.ratziel.module.item.impl.ItemManager
import cn.fd.ratziel.module.item.impl.builder.DefaultItemSerializer
import cn.fd.ratziel.module.item.impl.builder.NativeItemGenerator
import kotlinx.serialization.json.Json
import taboolib.common.platform.event.SubscribeEvent

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
object ItemElement : ElementHandler {

    val json by lazy {
        Json(baseJson) {
        }
    }

    override fun handle(element: Element) {

        val serializer = DefaultItemSerializer(json)
        val generator = NativeItemGenerator(element)

        val meta = serializer.deserialize(element.property)

        println(meta.display)

//        println(meta.characteristic)
//        println(meta.durability)
//        println(meta.nbt)

        println("————————————————————————————————")


        val test = generator.build()

//
//        println(testMeta)
//
//        println("————————————————————————————————")
//
//        println(serializer.usedNodes.toList().toString())
//
//        println("————————————————————————————————")
//
//        // 注册物品
//        ItemManager.registry[element.name] = generator
    }

    @SubscribeEvent
    fun onLoadStart(event: WorkspaceLoadEvent.Start) {
        // 清除注册的物品
        ItemManager.registry.clear()
    }

}
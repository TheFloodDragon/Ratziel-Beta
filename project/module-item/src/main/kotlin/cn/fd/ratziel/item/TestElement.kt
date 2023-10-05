package cn.fd.ratziel.item

import cn.fd.ratziel.common.element.registry.NewElement
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.element.api.ElementHandler
import cn.fd.ratziel.core.serialization.baseJson
import cn.fd.ratziel.item.meta.VItemMeta
import kotlinx.serialization.json.decodeFromJsonElement

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

        val meta = baseJson.decodeFromJsonElement<VItemMeta>(element.property)

        println(meta.displayName)
        println(meta.lore)

    } catch (ex: Exception) {
        ex.printStackTrace()
    }

}
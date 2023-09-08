package cn.fd.ratziel.bukkit.element.action

import cn.fd.ratziel.common.debug
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.element.ElementHandler
import cn.fd.ratziel.core.element.type.NewElement
import cn.fd.ratziel.core.element.util.ElementMemory
import cn.fd.ratziel.kether.KetherCompiler
import cn.fd.ratziel.kether.KetherHandler

/**
 * ActionElement
 *
 * @author TheFloodDragon
 * @since 2023/8/14 15:09
 */
@NewElement(
    name = "action",
    alias = ["actions"]
)
object ActionElement : ElementHandler, ElementMemory() {

    override fun handle(element: Element) {
        element.property?.let { json ->
            KetherCompiler.buildSection(json).toString().let {
                KetherHandler.invoke(it, null, emptyMap()).thenApply { result -> println(result) }
            }
        }
    }

}
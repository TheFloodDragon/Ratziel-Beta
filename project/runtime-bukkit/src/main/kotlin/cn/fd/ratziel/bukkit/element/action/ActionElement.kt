package cn.fd.ratziel.bukkit.element.action

import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.element.NewElement
import cn.fd.ratziel.core.element.api.LifeElementHandler
import cn.fd.ratziel.core.element.util.ElementMemory
import cn.fd.ratziel.kether.KetherCompiler
import cn.fd.ratziel.kether.KetherHandler
import taboolib.common.LifeCycle

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
object ActionElement : LifeElementHandler, ElementMemory() {

    override val lifeCycle= LifeCycle.ENABLE

    override fun handle(element: Element) {
        element.property.let { json ->
            KetherCompiler.buildSection(json).toString().let {
                KetherHandler.invoke(it, null, emptyMap()).thenApply { result -> println(result) }
            }
        }
    }

}
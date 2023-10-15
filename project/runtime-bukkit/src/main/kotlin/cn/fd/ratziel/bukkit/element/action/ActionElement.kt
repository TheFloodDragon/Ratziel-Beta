package cn.fd.ratziel.bukkit.element.action

import cn.fd.ratziel.common.annotation.OnLifeCycle
import cn.fd.ratziel.common.element.registry.NewElement
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.element.api.ElementHandler
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
@OnLifeCycle(LifeCycle.ENABLE)
object ActionElement : ElementHandler {

    override fun handle(element: Element) {
        element.property.let { json ->
            KetherCompiler.buildSection(json).toString().let {
                KetherHandler.invoke(it, null, emptyMap()).thenApply { result -> println(result) }
            }
        }
    }

}
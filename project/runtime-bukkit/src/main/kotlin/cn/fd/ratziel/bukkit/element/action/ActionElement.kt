package cn.fd.ratziel.bukkit.element.action

import cn.fd.ratziel.common.element.registry.ElementConfig
import cn.fd.ratziel.common.element.registry.NewElement
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.element.api.ElementHandler
import cn.fd.ratziel.function.SimpleArgumentContext
import cn.fd.ratziel.script.SimpleScriptEnv
import cn.fd.ratziel.script.block.GlobalBlockBuilder
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
@ElementConfig(LifeCycle.ENABLE)
object ActionElement : ElementHandler {

    override fun handle(element: Element) {
        element.property.let { json ->
            val block = GlobalBlockBuilder.parse(json)
            println(block)
            val result = block.execute(SimpleArgumentContext(SimpleScriptEnv()))
            println(result)
        }
    }

}
package cn.fd.ratziel.bukkit.element.action

import cn.fd.ratziel.common.element.registry.ElementConfig
import cn.fd.ratziel.common.element.registry.NewElement
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.element.api.ElementHandler
import cn.fd.ratziel.core.serialization.toBasic
import cn.fd.ratziel.script.ScriptBlockBuilder
import cn.fd.ratziel.script.ScriptManager
import cn.fd.ratziel.script.impl.SimpleScriptEnv
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
            val block = ScriptBlockBuilder.build(json.toBasic(), ScriptManager.defaultLanguage.executor)
            println(block)
            val result = block.evaluate(SimpleScriptEnv())
            println(result)
        }
    }

}
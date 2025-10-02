package cn.fd.ratziel.platform.bukkit.element.action

import cn.fd.ratziel.common.element.registry.ElementConfig
import cn.fd.ratziel.common.element.registry.NewElement
import cn.fd.ratziel.core.contextual.SimpleContext
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.element.ElementHandler
import cn.fd.ratziel.common.block.BlockBuilder
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
object ActionElement : ElementHandler.ParralHandler {

    override fun handle(element: Element) {
        val block = BlockBuilder.build(element)
        println(block)
        val result = block.execute(SimpleContext())
        println(result)
    }

}
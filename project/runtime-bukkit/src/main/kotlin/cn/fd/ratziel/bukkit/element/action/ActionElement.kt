package cn.fd.ratziel.bukkit.element.action

import cn.fd.ratziel.common.debug
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.element.ElementHandler
import cn.fd.ratziel.core.element.type.NewElement
import cn.fd.ratziel.core.element.util.ElementMemory

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
        addToMemory(element)
        debug("啊我艹我parse了")
    }

}
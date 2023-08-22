package cn.fd.utilities.bukkit.element

import cn.fd.utilities.common.debug
import cn.fd.utilities.core.element.Element
import cn.fd.utilities.core.element.api.ElementHandler
import cn.fd.utilities.core.element.type.NewElement
import cn.fd.utilities.core.element.util.ElementMemory

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
class ActionElement : ElementHandler, ElementMemory() {

    override fun handle(element: Element) {
        addToMemory(element)
        debug("啊我艹我parse了")
    }

}
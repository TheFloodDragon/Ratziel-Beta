package cn.fd.utilities.bukkit.element

import cn.fd.utilities.common.debug
import cn.fd.utilities.core.element.Element
import cn.fd.utilities.core.element.ElementHandler
import cn.fd.utilities.core.element.api.NewElement
import cn.fd.utilities.core.element.util.ElementMemory

/**
 * FDUtilities
 * cn.fd.utilities.bukkit.element.ActionElement
 *
 * @author: TheFloodDragon
 * @since 2023/8/14 15:09
 */
@NewElement(["action", "actions"])
class ActionElement : ElementHandler, ElementMemory() {

    override fun handle(element: Element) {
        addToMemory(element.space, element) //TODO 只是为了不报错的
        debug("啊我艹我parse了")
    }

}
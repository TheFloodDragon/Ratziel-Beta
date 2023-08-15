package cn.fd.utilities.bukkit.element

import cn.fd.utilities.common.debug
import cn.fd.utilities.core.element.Element
import cn.fd.utilities.core.element.api.ElementRegister
import cn.fd.utilities.core.element.parser.ElementHandler


/**
 * FDUtilities
 * cn.fd.utilities.bukkit.element.ActionElement
 *
 * @author: TheFloodDragon
 * @since 2023/8/14 15:09
 */
@ElementRegister(["action", "actions"])
class ActionElement : ElementHandler {

    override fun handle(element: Element) {
        debug("啊我艹我parse了")
    }

}
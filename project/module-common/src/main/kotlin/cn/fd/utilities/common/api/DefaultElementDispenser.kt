package cn.fd.utilities.common.api

import cn.fd.utilities.core.element.Element
import cn.fd.utilities.core.element.ElementService
import cn.fd.utilities.core.element.api.ElementDispenser

/**
 * DefaultElementDispenser
 *
 * @author: TheFloodDragon
 * @since 2023/8/22 14:11
 */
object DefaultElementDispenser : ElementDispenser {

    override fun dispense(element: Element) {
        return ElementService.getHandlers(element.type).forEach { it.handle(element) }
    }

}
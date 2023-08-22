package cn.fd.utilities.core.element.api

import cn.fd.utilities.core.element.Element
import taboolib.module.configuration.Configuration

/**
 * ElementMather
 *
 * @author: TheFloodDragon
 * @since 2023/8/22 14:15
 */
interface ElementMatcher {

    fun match(obj: Configuration): Set<Element>

}
package cn.fd.ratziel.common.event

import cn.fd.ratziel.core.element.Element
import taboolib.common.platform.event.ProxyEvent

/**
 * ElementLoadEvent - 元素加载事件
 *
 * @author TheFloodDragon
 * @since 2023/9/2 10:32
 */
class ElementLoadEvent(
    override var element: Element
) : ElementEvent, ProxyEvent()
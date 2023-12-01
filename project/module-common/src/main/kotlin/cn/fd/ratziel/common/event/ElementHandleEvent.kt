package cn.fd.ratziel.common.event

import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.element.api.ElementHandler
import taboolib.common.platform.event.ProxyEvent

/**
 * ElementHandleEvent - 元素处理事件
 *
 * @author TheFloodDragon
 * @since 2023/9/2 10:56
 */
class ElementHandleEvent(
    override var element: Element,
    /**
     * 处理该元素的处理器
     */
    val handler: ElementHandler
) : ElementEvent, ProxyEvent()
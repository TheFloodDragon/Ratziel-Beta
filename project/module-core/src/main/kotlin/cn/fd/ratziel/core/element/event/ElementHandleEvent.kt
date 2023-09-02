package cn.fd.ratziel.core.element.event

import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.element.ElementHandler
import taboolib.common.platform.event.ProxyEvent

/**
 * ElementHandleEvent
 * 处理元素时触发
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
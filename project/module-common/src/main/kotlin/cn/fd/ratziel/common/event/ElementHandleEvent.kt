package cn.fd.ratziel.common.event

import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.element.api.ElementHandler
import taboolib.common.event.CancelableInternalEvent

/**
 * ElementHandleEvent
 *
 * @author TheFloodDragon
 * @since 2024/4/4 21:11
 */
class ElementHandleEvent(
    /**
     * 要处理的元素
     */
    val element: Element,
    /**
     * 处理该元素的处理器
     */
    val handler: ElementHandler
) : CancelableInternalEvent()
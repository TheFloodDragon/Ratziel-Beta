package cn.fd.ratziel.common.event

import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.element.ElementHandler
import taboolib.common.event.InternalEvent

/**
 * ElementEvaluateEvent
 *
 * @author TheFloodDragon
 * @since 2025/5/17 16:47
 */
sealed class ElementEvaluateEvent : InternalEvent() {

    class Start(
        /**
         * 处理此元素的 [ElementHandler]
         */
        val handler: ElementHandler,
        /**
         * 交给该 [ElementHandler] 解析的所有元素
         */
        val elements: Collection<Element>,
    ) : ElementEvaluateEvent()

    class Process(
        /**
         * 处理此元素的 [ElementHandler]
         */
        val handler: ElementHandler,
        /**
         * 处理的元素
         */
        val element: Element,
    ) : ElementEvaluateEvent()

    class End(
        /**
         * 处理此元素的 [ElementHandler]
         */
        val handler: ElementHandler,
    ) : ElementEvaluateEvent()

}
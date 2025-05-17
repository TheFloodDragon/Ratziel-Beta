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

    class Start internal constructor(
        /**
         * 处理此元素的 [ElementHandler]
         */
        val handler: ElementHandler,
        /**
         * 交给该 [ElementHandler] 解析的所有元素
         */
        val elements: Collection<Element>,
    ) : ElementEvaluateEvent() {
        init {
            // 触发 ElementHandler#onStart
            handler.onStart(elements)
        }
    }

    class Process internal constructor(
        /**
         * 处理此元素的 [ElementHandler]
         */
        val handler: ElementHandler,
        /**
         * 处理的元素
         */
        val element: Element,
    ) : ElementEvaluateEvent()

    class End internal constructor(
        /**
         * 处理此元素的 [ElementHandler]
         */
        val handler: ElementHandler,
    ) : ElementEvaluateEvent() {
        init {
            // 触发 ElementHandler#onEnd
            handler.onEnd()
        }
    }

}
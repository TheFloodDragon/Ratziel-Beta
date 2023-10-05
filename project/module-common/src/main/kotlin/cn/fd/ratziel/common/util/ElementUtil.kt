package cn.fd.ratziel.common.util

import cn.fd.ratziel.common.element.evaluator.ApexElementEvaluator
import cn.fd.ratziel.common.event.api.ElementHandleEvent
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.element.service.ElementRegistry
import cn.fd.ratziel.core.util.callThenRun
import taboolib.common.platform.function.severe

/**
 * 处理元素
 */
fun Element.handle() {
    ElementRegistry.runWithHandlers(this.type) { (_, handler) ->
        ElementHandleEvent(this, handler).callThenRun {
            try {
                ApexElementEvaluator.eval(handler, this)
            } catch (e: Exception) {
                severe("Couldn't handle element $this by $handler")
                e.printStackTrace()
            }
        }
    }
}
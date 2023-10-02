package cn.fd.ratziel.common.util

import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.element.ElementService
import cn.fd.ratziel.core.element.api.LifeElementHandler
import cn.fd.ratziel.common.event.api.ElementHandleEvent
import cn.fd.ratziel.core.util.callThenRun
import taboolib.common.platform.function.postpone
import taboolib.common.platform.function.severe

/**
 * 处理元素
 */
fun Element.handle() {
    ElementService.getHandlers(this.type).forEach { handler ->
        ElementHandleEvent(this, handler).callThenRun {
            try {
                fun run() = handler.handle(this)
                if (handler is LifeElementHandler)
                    postpone(handler.lifeCycle) { run() }
                else run()
            } catch (e: Exception) {
                severe("Couldn't handle element $this by $handler")
            }
        }
    }
}
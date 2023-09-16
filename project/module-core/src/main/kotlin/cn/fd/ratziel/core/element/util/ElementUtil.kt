package cn.fd.ratziel.core.element.util

import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.element.type.ElementService
import cn.fd.ratziel.core.event.ElementHandleEvent
import cn.fd.ratziel.core.util.callThenRun
import taboolib.common.platform.function.severe

/**
 * 处理元素
 */
fun Element.handle() {
    ElementService.getHandlers(type).forEach { handler ->
        ElementHandleEvent(this, handler).callThenRun {
            try {
                handler.handle(this)
            } catch (e: Exception) {
                severe("Couldn't handle element $this by $handler")
            }
        }
    }
}
package cn.fd.ratziel.core.element.util

import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.element.event.ElementHandleEvent
import cn.fd.ratziel.core.element.type.ElementService
import cn.fd.ratziel.core.util.callThenRun

/**
 * 处理元素
 */
fun Element.handle() {
    ElementService.getHandlers(type).forEach { handler ->
        ElementHandleEvent(this, handler).callThenRun {
            handler.handle(this)
        }
    }
}
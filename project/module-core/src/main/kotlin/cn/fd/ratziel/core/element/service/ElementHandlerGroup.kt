package cn.fd.ratziel.core.element.service

import cn.fd.ratziel.core.Priority
import cn.fd.ratziel.core.element.api.ElementHandler
import cn.fd.ratziel.core.util.priority
import java.util.concurrent.ConcurrentSkipListSet


/**
 * ElementHandlerGroup - 元素处理器组
 *
 * @author TheFloodDragon
 * @since 2023/10/4 15:28
 */
class ElementHandlerGroup {

    /**
     * 处理器表
     */
    val handlers: MutableCollection<Priority<ElementHandler>> = ConcurrentSkipListSet(compareBy { it.priority })

    /**
     * 注册元素处理器
     */
    fun register(handler: ElementHandler, priority: Byte) = handlers.add(handler priority priority)

    /**
     * 取消注册元素处理器
     */
    fun unregister(handler: ElementHandler) = handlers.removeIf { it.value == handler }

}
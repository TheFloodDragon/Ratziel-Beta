package cn.fd.ratziel.core.element.service

import cn.fd.ratziel.core.element.api.ElementHandler
import java.util.*


/**
 * ElementHandlerGroup
 * 元素处理器组
 *
 * @author TheFloodDragon
 * @since 2023/10/4 15:28
 */
class ElementHandlerGroup {

    /**
     * 处理器表
     */
    val handlerMap: NavigableMap<Byte, MutableList<ElementHandler>> = Collections.synchronizedNavigableMap(TreeMap())

    /**
     * 注册元素处理器
     */
    fun register(handler: ElementHandler, priority: Byte) =
        handlerMap.computeIfAbsent(priority) { mutableListOf() }.add(handler)

    /**
     * 取消注册元素处理器
     */
    fun unregister(handler: ElementHandler) =
        handlerMap.values.forEach { list -> list.removeIf { it == handler } }

}
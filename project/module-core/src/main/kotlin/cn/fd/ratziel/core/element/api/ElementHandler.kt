package cn.fd.ratziel.core.element.api

import cn.fd.ratziel.core.element.Element

/**
 * ElementHandler - 元素处理器
 *
 * @author TheFloodDragon
 * @since 2024/4/21 9:46
 */
interface ElementHandler {

    /**
     * 处理元素
     */
    fun handle(element: Element)

}
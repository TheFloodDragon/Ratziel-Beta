package cn.fd.ratziel.core.element.api

import cn.fd.ratziel.core.element.Element

/**
 * 元素的处理器
 */
interface ElementHandler {

    /**
     * 处理元素
     */
    fun handle(element: Element)

}
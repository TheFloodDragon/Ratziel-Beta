package cn.fd.utilities.core.element.api

import cn.fd.utilities.core.element.Element

/**
 * 元素的解析器
 */
interface ElementHandler {

    /**
     * 解析函数
     */
    fun handle(element: Element)

}
package cn.fd.ratziel.core.element

/**
 * 元素的解析器
 */
interface ElementHandler {

    /**
     * 解析函数
     */
    fun handle(element: Element)

}
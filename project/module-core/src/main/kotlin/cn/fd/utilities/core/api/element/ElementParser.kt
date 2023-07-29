package cn.fd.utilities.core.api.element

/**
 * 元素的解析器
 */
interface ElementParser {

    /**
     * 元素下项的解析器
     */
    val keyParser: KeyParser

    /**
     * 解析函数
     */
    fun parse(): Element?

}
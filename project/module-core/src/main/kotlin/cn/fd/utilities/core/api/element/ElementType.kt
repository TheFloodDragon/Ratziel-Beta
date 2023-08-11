package cn.fd.utilities.core.api.element

import cn.fd.utilities.core.api.element.parser.ElementParser

interface ElementType {

    /**
     * 元素类型的称谓
     * 以及其别称
     */
    val name: Array<String>

    /**
     * 元素的解析器
     */
    val parser: ElementParser

}
package cn.fd.utilities.core.element

import cn.fd.utilities.core.element.parser.ElementHandler
import cn.fd.utilities.core.util.strValue

class ElementType(

    /**
     * 元素类型的称谓
     * 以及其别称
     */
    val name: Array<String>,

    /**
     * 处理该元素类型的处理器
     */
    val handler: ElementHandler,

    ) {

    override fun toString(): String {
        return this::class.java.strValue()
    }

}
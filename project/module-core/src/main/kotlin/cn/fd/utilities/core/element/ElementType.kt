package cn.fd.utilities.core.element

import cn.fd.utilities.core.element.parser.ElementHandler

class ElementType(

    /**
     * 元素类型的名称
     * 既其标识符
     */
    val name: Array<String>,

    /**
     * 处理该元素类型的处理器
     */
    val handlers: Array<ElementHandler>,

    ) {

    override fun toString(): String {
        return this::class.java.simpleName + '{' + "name=" + name.toList()
            .toString() + " ; " + "handlers=" + handlers.toList().toString()
    }

}
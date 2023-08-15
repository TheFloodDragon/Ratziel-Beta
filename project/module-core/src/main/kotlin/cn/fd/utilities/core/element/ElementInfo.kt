package cn.fd.utilities.core.element

import cn.fd.utilities.core.element.parser.ElementHandler

/**
 * 元素的信息
 * 包含元素名称和处理该元素类型的处理器
 */
class ElementInfo(

    /**
     * 元素名称列表
     */
    var names: Array<String>,

    /**
     * 处理该元素类型的处理器
     */
    var handlers: Array<ElementHandler>,

    ) {

    override fun toString(): String {
        return this::class.java.simpleName + '{' + "name=" + names.toList()
            .toString() + ";" + "handlers=" + handlers.toList().toString()
    }

}
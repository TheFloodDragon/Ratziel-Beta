package cn.fd.utilities.core.element.util

import cn.fd.utilities.core.element.Element
import cn.fd.utilities.core.element.ElementType
import cn.fd.utilities.core.memory.HashMapMemory

/**
 * ElementMemory
 *
 * @author: TheFloodDragon
 * @since 2023/8/21 10:30
 */
open class ElementMemory : HashMapMemory<ElementType, Element>() {

    /**
     * 添加进容器
     */
    fun addToMemory(element: Element) {
        addToMemory(element.type, element)
    }

}
package cn.fd.ratziel.core.element.util

import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.element.ElementType
import cn.fd.ratziel.core.memory.HashMapMemory

/**
 * ElementMemory
 *
 * @author TheFloodDragon
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
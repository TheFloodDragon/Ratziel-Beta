@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.core.serialization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.elementNames
import kotlinx.serialization.json.JsonNames

/**
 * 获取序列化器中使用的节点
 * 即所有的元素名称和 [JsonNames] 注解中的所有名称
 */
val SerialDescriptor.elementAlias: List<String>
    get() {
        val annoNames = buildSet {
            for (anno in elementAnnotations) {
                anno.forEach { if (it is JsonNames) addAll(it.names) }
            }
        }
        return elementNames.plus(annoNames)
    }

/**
 * @return 所有元素注解的迭代器
 */
val SerialDescriptor.elementAnnotations: Iterable<List<Annotation>>
    get() = Iterable {
        object : Iterator<List<Annotation>> {
            private var elementsLeft = elementsCount
            override fun hasNext(): Boolean = elementsLeft > 0
            override fun next() = getElementAnnotations(elementsCount - (elementsLeft--))
        }
    }
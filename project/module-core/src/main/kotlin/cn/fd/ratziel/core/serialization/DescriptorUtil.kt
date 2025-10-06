@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.core.serialization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.elementNames
import kotlinx.serialization.json.JsonNames

/**
 * 获取序列化器中使用的节点
 * 即所有的元素名称和 [JsonNames] 注解中的所有名称
 */
val SerialDescriptor.elementNodes: Set<String>
    get() = buildSet {
        // 加入元素的本名
        addAll(elementNames)
        // 加入注解的名称
        for (annotations in elementAnnotations) {
            for (anno in annotations) {
                // JsonNames 修饰的别名
                if (anno is JsonNames) addAll(anno.names)
                // 指定的序列化名称
                else if (anno is SerialName) add(anno.value)
            }
        }
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
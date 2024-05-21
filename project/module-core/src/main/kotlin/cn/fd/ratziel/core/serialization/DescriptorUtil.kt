@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.core.serialization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.elementNames
import kotlinx.serialization.json.JsonNames

fun SerialDescriptor.getElementNames(name: String): Set<String> = this.getElementNames(this.getElementIndex(name))

fun SerialDescriptor.getElementAnnotations(name: String) = this.getElementAnnotations(this.getElementIndex(name))

fun SerialDescriptor.getElementDescriptor(name: String) = this.getElementDescriptor(this.getElementIndex(name))

/**
 * 获取序列化器中使用的节点
 * 即所有的元素名称和 [JsonNames] 注解中的所有名称
 */
val SerialDescriptor.elementAlias: List<String> get() = elementNames.flatMap { getElementNames(it) }

/**
 * 获取序列化器中使用的节点
 * 即的元素名称和 [JsonNames] 注解中的名称
 */
fun SerialDescriptor.getElementNames(index: Int): Set<String> =
    buildSet {
        // 添加元素名称
        add(getElementName(index))
        // 添加注解 [JsonNames] 中的名称
        getElementAnnotations(index).forEach { if (it is JsonNames) addAll(it.names) }
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
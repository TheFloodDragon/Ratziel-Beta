@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.module.item.util

import cn.fd.ratziel.core.serialization.elementAnnotations
import cn.fd.ratziel.core.util.forEachDually
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.elementNames
import kotlinx.serialization.json.JsonNames

/**
 * 获取序列化器中使用的节点
 * 即所有的元素名称和 [JsonNames] 注解中的所有名称
 */
val SerialDescriptor.usedNodes: List<String>
    get() = buildList {
        // 添加所有元素名称
        addAll(elementNames)
        // 添加注解 [JsonNames] 中的名称
        elementAnnotations.forEachDually { if (it is JsonNames) addAll(it.names) }
    }
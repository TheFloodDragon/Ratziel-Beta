package cn.fd.ratziel.module.itemengine.api.builder

import cn.fd.ratziel.core.function.UnsupportedTypeException
import cn.fd.ratziel.core.serialization.elementAnnotations
import cn.fd.ratziel.core.util.forEachDually
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.elementNames
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNames

/**
 * ItemSerializer - 用于从配置文件中序列成物品
 *
 * @author TheFloodDragon
 * @since 2023/10/28 13:24
 */
interface ItemSerializer<T> {

    /**
     * 占用的节点 (仅第一层)
     */
    val usedNodes: Array<String>

    /**
     * 序列化成 [JsonElement]
     */
    fun serializeToJson(value: T): JsonElement

    /**
     * 反序列化 ( 通过 [JsonElement] )
     */
    fun deserializeFromJson(element: JsonElement): T

    companion object {

        /**
         * 获取序列化器中使用的节点
         * 即所有的元素名称和 [JsonNames] 注解中的所有名称
         */
        @OptIn(ExperimentalSerializationApi::class)
        @JvmStatic
        fun getUsedNodes(descriptor: SerialDescriptor) = buildList {
            // 添加所有元素名称
            addAll(descriptor.elementNames)
            descriptor.elementAnnotations.forEachDually {
                // 添加注解 [JsonNames] 中的名称
                if (it is JsonNames) addAll(it.names)
            }
        }

        @JvmStatic
        fun getUsedNodes(serializer: Any) = when (serializer) {
            is KSerializer<*> -> getUsedNodes(serializer.descriptor)
            is ItemSerializer<*> -> serializer.usedNodes.toList()
            is SerialDescriptor -> getUsedNodes(serializer)
            else -> throw UnsupportedTypeException(serializer)
        }

    }

}
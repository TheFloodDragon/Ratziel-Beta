package cn.fd.ratziel.module.item.impl.builder.provided

import cn.fd.ratziel.core.functional.MutexedValue
import cn.fd.ratziel.module.item.api.NeoItem
import cn.fd.ratziel.module.item.api.builder.ItemInterpreter
import cn.fd.ratziel.module.item.api.builder.ItemStream
import cn.fd.ratziel.module.item.api.component.ItemComponentType
import cn.fd.ratziel.module.item.impl.component.ItemComponents
import cn.fd.ratziel.module.item.util.asComponentData
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonElement
import taboolib.common.platform.function.severe

/**
 * ComponentInterpreter
 *
 * @author TheFloodDragon
 * @since 2025/7/24 11:40
 */
object ComponentInterpreter : ItemInterpreter {

    override suspend fun interpret(stream: ItemStream) {
        // 序列化任务: 元素(解析过后的) -> 组件 -> 数据
        val element = stream.fetchProperty()
        val serializationTasks = parallelSerialize(element, stream.item)
        // 等待所有序列化任务完成
        serializationTasks.joinAll()
    }

    /**
     * 并行序列化
     */
    suspend fun parallelSerialize(element: JsonElement, item: MutexedValue<NeoItem>) = coroutineScope {
        val tasks = linkedSetOf<Job>()
        // 采用选中的组件 (提升效率)
        for (component in ItemComponents.registry) {
            // 不支持的直接跳过
            if (!component.isSupported) continue
            // 类型适配
            @Suppress("UNCHECKED_CAST")
            component as ItemComponentType<Any>
            // 添加任务
            tasks += launch {
                val generated = try {
                    component.transforming.jsonTransformer.fromJsonElement(element)
                } catch (ex: Throwable) {
                    severe("Failed to deserialize item component '${component.id}' from json element: $element")
                    ex.printStackTrace()
                    null
                } ?: return@launch

                item.withValue {
                    it.data.asComponentData()[component] = generated
                }
            }
        }
        return@coroutineScope tasks
    }

}
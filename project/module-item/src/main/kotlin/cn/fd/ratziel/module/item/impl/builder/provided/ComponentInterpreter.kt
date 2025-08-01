package cn.fd.ratziel.module.item.impl.builder.provided

import cn.altawk.nbt.tag.NbtCompound
import cn.fd.ratziel.core.functional.SynchronizedValue
import cn.fd.ratziel.module.item.ItemRegistry
import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.builder.ItemInterpreter
import cn.fd.ratziel.module.item.api.builder.ItemStream
import cn.fd.ratziel.module.item.util.ComponentConverter
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonElement

/**
 * ComponentInterpreter
 *
 * @author TheFloodDragon
 * @since 2025/7/24 11:40
 */
object ComponentInterpreter : ItemInterpreter {

    override suspend fun interpret(stream: ItemStream) = coroutineScope {
        // 序列化任务: 元素(解析过后的) -> 组件 -> 数据
        val element = stream.fetchElement()
        val serializationTasks = parallelSerialize(element, stream.data)
        // 等待所有序列化任务完成
        serializationTasks.joinAll()
    }

    /**
     * 并行序列化
     */
    @JvmStatic
    suspend fun parallelSerialize(element: JsonElement, data: SynchronizedValue<out ItemData>) = coroutineScope {
        ItemRegistry.registry.map { integrated ->
            launch {
                val generated = ComponentConverter.transformToNbtTag(integrated, element).getOrNull()
                // 合并数据
                if (generated as? NbtCompound != null) data.withValue {
                    // 合并标签
                    it.tag.merge(generated, true)
                }
            }
        }
    }

}
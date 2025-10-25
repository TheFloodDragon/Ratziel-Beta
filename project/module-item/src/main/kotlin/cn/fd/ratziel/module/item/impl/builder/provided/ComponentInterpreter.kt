package cn.fd.ratziel.module.item.impl.builder.provided

import cn.altawk.nbt.tag.NbtCompound
import cn.fd.ratziel.core.functional.MutexedValue
import cn.fd.ratziel.module.item.ItemRegistry
import cn.fd.ratziel.module.item.api.NeoItem
import cn.fd.ratziel.module.item.api.builder.ItemInterpreter
import cn.fd.ratziel.module.item.api.builder.ItemStream
import cn.fd.ratziel.module.item.util.ComponentConverter
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

/**
 * ComponentInterpreter
 *
 * @author TheFloodDragon
 * @since 2025/7/24 11:40
 */
class ComponentInterpreter : ItemInterpreter {

    /**
     * 此物品用到的组件列表
     */
    val detectedComponents = hashSetOf<ItemRegistry.ComponentIntegrated<*>>()

    /**
     * 寻找确定的用到的组件.
     * 在 [preFlow] 阶段确定要用到的组件, 可以避免没有用的的组件被序列化耗时, 提升效率,
     * 于此同时, 也将意味着物品生成的过程中无法新增别的组件.
     */
    override suspend fun preFlow(stream: ItemStream) {
        val properties = stream.fetchProperty() as? JsonObject ?: return
        for (key in properties.keys) {
            // 寻找包含此节点名称的组件
            val component = ItemRegistry.registry.find {
                it.elementNodes.contains(key)
            }
            if (component != null) {
                // 选中添加
                detectedComponents += component
            }
        }
    }

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
        // 采用选中的组件 (提升效率)
        detectedComponents.map { integrated ->
            launch {
                val generated = ComponentConverter.transformToNbtTag(integrated, element).getOrNull()
                // 合并数据
                if (generated as? NbtCompound != null) item.withValue {
                    // 合并标签 (覆盖原始数据)
                    it.data.tag.merge(generated, true)
                }
            }
        }
    }

}
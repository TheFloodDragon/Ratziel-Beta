package cn.fd.ratziel.module.item.internal

import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.module.item.api.NeoItem
import cn.fd.ratziel.module.item.api.builder.ItemSource
import cn.fd.ratziel.module.item.api.builder.ItemStream
import kotlinx.coroutines.*

/**
 * SourceInterpreter
 *
 * @author TheFloodDragon
 * @since 2025/5/17 14:38
 */
class SourceInterpreter(val source: ItemSource) {

    suspend fun interpret(stream: ItemStream): NeoItem? {
        val element = Element(stream.origin.identifier, stream.fetchElement())
        // 生成物品
        val item = source.generateItem(element, stream.context) ?: return null
        // 写入数据
        val newTag = item.data.tag
        val targetMaterial = item.data.material
        val targetAmount = item.data.amount
        // 合并任务
        stream.data.withValue {
            // 设置材料
            if (!targetMaterial.isEmpty()) it.material = targetMaterial
            // 设置数量
            if (targetAmount > 1) it.amount = targetAmount
            // 合并标签
            if (newTag.isNotEmpty()) it.tag.merge(newTag, true)
        }
        return item
    }

    companion object {

        /**
         * 并行解释物品源表
         */
        @JvmStatic
        suspend fun parallelInterpret(sources: List<ItemSource>, stream: ItemStream): Job = coroutineScope {
            launch {
                // 物品源解释任务
                val tasks = sources.map {
                    // 并行解释每个物品源
                    async { SourceInterpreter(it).interpret(stream) }
                }

                // 等待所有任务完成并收集数据
                val results = tasks.awaitAll().mapNotNull { it?.data }

                // 物品材料重排序
                stream.data.withValue { data ->
                    for (targetData in results) {
                        if (!targetData.material.isEmpty()) {
                            data.material = targetData.material
                            break
                        }
                    }
                    for (targetData in results) {
                        if (targetData.amount > 1) {
                            data.amount = targetData.amount
                            break
                        }
                    }
                }

            }
        }

    }

}
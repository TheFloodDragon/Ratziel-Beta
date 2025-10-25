package cn.fd.ratziel.module.item.impl.builder.provided

import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.module.item.api.NeoItem
import cn.fd.ratziel.module.item.api.builder.ItemSource
import cn.fd.ratziel.module.item.api.builder.ItemStream
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

/**
 * SourceInterpreter
 *
 * @author TheFloodDragon
 * @since 2025/5/17 14:38
 */
object SourceInterpreter {

    /**
     * 使用物品源 [source] 解释物品生成流
     */
    suspend fun interpret(source: ItemSource, stream: ItemStream): NeoItem? {
        val element = Element(stream.origin.identifier, stream.fetchProperty())
        // 生成物品
        val item = source.generateItem(element, stream.context) ?: return null
        // 写入数据
        val newTag = item.data.tag
        val targetMaterial = item.data.material
        val targetAmount = item.data.amount
        // 合并任务
        stream.item.useValue {
            // 设置材料
            if (!targetMaterial.isEmpty()) data.material = targetMaterial
            // 设置数量
            if (targetAmount > 1) data.amount = targetAmount
            // 合并标签
            if (newTag.isNotEmpty()) data.tag.merge(newTag, true)
        }
        return item
    }

    /**
     * 并行解释物品源表
     */
    @JvmStatic
    suspend fun parallelInterpret(sources: List<ItemSource>, stream: ItemStream) = coroutineScope {
        // 物品源解释任务
        val tasks = sources.map { source ->
            // 并行解释每个物品源
            async { interpret(source, stream) }
        }

        // 等待所有任务完成并收集数据
        val results = tasks.awaitAll().mapNotNull { it?.data }

        // 物品材料重排序
        stream.item.useValue {
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

package cn.fd.ratziel.module.item.internal

import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.NeoItem
import cn.fd.ratziel.module.item.api.builder.ItemSource
import cn.fd.ratziel.module.item.api.builder.ItemStream

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
         * 物品材料重排序
         */
        suspend fun sequenceMaterial(stream: ItemStream, results: List<NeoItem?>) {
            stream.data.withValue { data ->
                sequenceMaterial(data, results.mapNotNull { it?.data })
            }
        }

        /**
         * 物品材料重排序
         */
        fun sequenceMaterial(data: ItemData, results: List<ItemData>) {
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
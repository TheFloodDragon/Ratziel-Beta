package cn.fd.ratziel.module.item.internal.builder

import cn.fd.ratziel.module.item.api.builder.ItemInterceptor
import cn.fd.ratziel.module.item.api.builder.ItemSource
import cn.fd.ratziel.module.item.api.builder.ItemStream

/**
 * SourceInterceptor
 *
 * @author TheFloodDragon
 * @since 2025/5/17 14:38
 */
class SourceInterceptor(val source: ItemSource) : ItemInterceptor {

    override suspend fun intercept(stream: ItemStream) {
        // 生成物品
        val item = source.generateItem(stream.origin, stream.context) ?: return
        // 写入数据
        val newTag = item.data.tag
        stream.data.withValue {
            // 设置材料
            val targetMaterial = item.data.material
            if (!targetMaterial.isEmpty()) it.material = targetMaterial
            // 设置数量
            val targetAmount = item.data.amount
            if (targetAmount > 1) it.amount = targetAmount
            // 合并标签
            it.tag.merge(newTag, true)
        }
    }

}
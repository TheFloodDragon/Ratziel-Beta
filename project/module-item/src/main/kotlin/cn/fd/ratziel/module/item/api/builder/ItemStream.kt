package cn.fd.ratziel.module.item.api.builder

import cn.fd.ratziel.core.Identifier
import cn.fd.ratziel.core.contextual.ArgumentContext
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.functional.MutexedValue
import cn.fd.ratziel.core.serialization.json.JsonTree
import cn.fd.ratziel.module.item.api.ItemData
import kotlinx.serialization.json.JsonElement

/**
 * ItemStream
 *
 * @author TheFloodDragon
 * @since 2025/5/14 21:09
 */
interface ItemStream {

    /**
     * 原始元素
     */
    val origin: Element

    /**
     * 物品标识符
     */
    val identifier: Identifier

    /**
     * [JsonElement] 树
     * 由解释器处理, 供序列化使用
     */
    val tree: MutexedValue.Mutable<JsonTree>

    /**
     * 物品数据 (最终产物)
     */
    val data: MutexedValue<ItemData>

    /**
     * 上下文
     */
    var context: ArgumentContext

    /**
     * 复制流
     */
    suspend fun copy(): ItemStream

    /**
     * 返回包含 [tree] 当前的元素内容的 [Element]
     * @return [Element]
     */
    suspend fun fetchElement(): Element {
        return origin.copyOf(fetchProperty())
    }

    /**
     * 返回 [tree] 当前的元素内容
     * @return [JsonElement]
     */
    suspend fun fetchProperty(): JsonElement {
        return tree.withValue { it.toElement() }
    }

    /**
     * 更新 [tree] 的元素内容
     */
    suspend fun updateProperty(element: JsonElement) {
        return tree.update { JsonTree(element) }
    }

}
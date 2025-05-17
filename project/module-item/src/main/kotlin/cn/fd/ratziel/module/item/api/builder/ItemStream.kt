package cn.fd.ratziel.module.item.api.builder

import cn.fd.ratziel.core.Identifier
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.core.function.SynchronizedValue
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
    val tree: SynchronizedValue<JsonTree>

    /**
     * 物品数据 (最终产物)
     */
    val data: SynchronizedValue<ItemData>

    /**
     * 上下文
     */
    val context: ArgumentContext

    /**
     * 返回 [tree] 当前的元素内容
     * @return [JsonElement]
     */
    suspend fun fetchElement(): JsonElement {
        return tree.withValue { it.toElement() }
    }

}
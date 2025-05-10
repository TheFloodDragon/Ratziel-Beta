package cn.fd.ratziel.module.item.api.builder

import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.module.item.api.ItemData
import kotlinx.serialization.json.JsonElement

/**
 * ItemInterceptor
 *
 * @author TheFloodDragon
 * @since 2025/5/10 15:22
 */
interface ItemInterceptor {

    /**
     * 解释元素
     *
     * @param element 要解释的 [JsonElement]
     * @param context 上下文
     * @return 物品数据 [ItemData]
     */
    fun intercept(element: JsonElement, context: ArgumentContext): ItemData?

}
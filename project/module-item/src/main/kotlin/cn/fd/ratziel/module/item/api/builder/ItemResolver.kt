package cn.fd.ratziel.module.item.api.builder

import cn.fd.ratziel.core.function.ArgumentContext
import kotlinx.serialization.json.JsonElement

/**
 * ItemResolver
 *
 * @author TheFloodDragon
 * @since 2025/5/3 19:46
 */
interface ItemResolver {

    /**
     * 解析处理 [JsonElement]
     *
     * @param element 要解析的 [JsonElement]
     * @param context 上下文
     * @return 解析完的 [JsonElement]
     */
    fun resolve(element: JsonElement, context: ArgumentContext): JsonElement

}
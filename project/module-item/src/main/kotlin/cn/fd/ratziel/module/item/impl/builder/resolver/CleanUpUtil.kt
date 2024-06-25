package cn.fd.ratziel.module.item.impl.builder.resolver

import cn.fd.ratziel.core.serialization.asMutable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import java.util.function.Function

/**
 * CleanUpUtil
 *
 * @author TheFloodDragon
 * @since 2024/6/25 20:03
 */
object CleanUpUtil {

    @JvmStatic
    fun handleOnFilter(
        element: JsonObject,
        accessibleNodes: Iterable<String>,
        action: Function<Map.Entry<String, JsonElement>, JsonElement>
    ): JsonObject {
        val builder = element.asMutable()
        for (entry in builder) {
            // 在允许的节点范围内
            if (entry.key in accessibleNodes) {
                builder[entry.key] = action.apply(entry) // 通过操作替换节点
            }
        }
        return builder.asImmutable()
    }

    @JvmStatic
    fun handleOnFilter(element: JsonElement, accessibleNodes: Iterable<String>, action: Function<Map.Entry<String, JsonElement>, JsonElement>): JsonElement =
        if (element is JsonObject) handleOnFilter(element, accessibleNodes, action) else element

}
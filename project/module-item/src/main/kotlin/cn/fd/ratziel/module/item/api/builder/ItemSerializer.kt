package cn.fd.ratziel.module.item.api.builder

import cn.fd.ratziel.function.ArgumentContext
import kotlinx.serialization.json.JsonElement

/**
 * ItemSerializer - 物品序列化器
 *
 * @author TheFloodDragon
 * @since 2024/4/4 19:50
 */
interface ItemSerializer<T> {

    /**
     * 序列化 - 组件[T]到[JsonElement]
     */
    fun serialize(component: T, context: ArgumentContext): JsonElement

    /**
     * 反序列化 - [JsonElement]到组件[T]
     */
    fun deserialize(element: JsonElement, context: ArgumentContext): T

}
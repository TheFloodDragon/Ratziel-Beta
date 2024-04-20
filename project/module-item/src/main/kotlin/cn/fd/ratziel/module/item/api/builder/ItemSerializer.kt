package cn.fd.ratziel.module.item.api.builder

import cn.fd.ratziel.module.item.api.ItemComponent
import kotlinx.serialization.json.JsonElement

/**
 * ItemSerializer - 物品序列化器
 *
 * @author TheFloodDragon
 * @since 2024/4/4 19:50
 */
interface ItemSerializer<T : ItemComponent> {

    /**
     * 序列化 - 组件[T]到[JsonElement]
     */
    fun serialize(component: T): JsonElement

    /**
     * 反序列化 - [JsonElement]到组件[T]
     */
    fun deserialize(element: JsonElement): T

    /**
     * 获取被该物品序列化器所使用(占用)的节点
     */
    fun getOccupiedNodes(): Array<String>

}
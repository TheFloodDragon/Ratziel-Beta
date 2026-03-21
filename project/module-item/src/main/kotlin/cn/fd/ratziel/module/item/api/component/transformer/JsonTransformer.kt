package cn.fd.ratziel.module.item.api.component.transformer

import kotlinx.serialization.json.JsonElement

/**
 * JsonTransformer - [JsonElement] 数据类型转换
 *
 * @author TheFloodDragon
 * @since 2026/1/1 21:27
 */
interface JsonTransformer<T> {

    /**
     * 组件 -> [JsonElement]
     */
    fun toJsonElement(component: T): JsonElement

    /**
     * [JsonElement] -> 组件
     *
     * @return 传入数据不合法或者没有被转换信息时, 可返回 null
     */
    fun formJsonElement(element: JsonElement): T?

}
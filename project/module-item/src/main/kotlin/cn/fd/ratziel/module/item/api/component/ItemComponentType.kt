package cn.fd.ratziel.module.item.api.component

import cn.altawk.nbt.tag.NbtTag
import kotlinx.serialization.json.JsonElement

/**
 * ItemComponentType - 物品组件类型
 * 
 * @author TheFloodDragon
 * @since 2025/11/29 21:48
 */
interface ItemComponentType<T> {

    /**
     * 组件标识符 (插件内部命名)
     */
    val key: String

    /**
     * 组件数据类型转换器
     */
    val transformer: Transformer<T>

    /**
     * Transformer - 组件数据类型转换器
     *
     * 一个基本的组件必须实现 [JsonTransformer] 和 [NbtTransformer] 两种数据转换器.
     */
    interface Transformer<T> : JsonTransformer<T>, NbtTransformer<T>

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
        fun transformToJson(tar: T): JsonElement

        /**
         * [JsonElement] -> 组件
         *
         * @return 传入数据不合法或者没有被转换信息时, 可返回 null
         */
        fun detransformFromJson(src: JsonElement): T?

    }

    /**
     * NbtTransformer - [NbtTag] 数据类型转换
     *
     * @author TheFloodDragon
     * @since 2026/1/1 21:27
     */
    interface NbtTransformer<T> {

        /**
         * 组件 -> [NbtTag]
         */
        fun transformToNbtTag(tar: T): NbtTag

        /**
         * [NbtTag] -> 组件
         *
         * @return 传入数据不合法或者没有被转换信息时, 可返回 null
         */
        fun detransformFromNbtTag(src: NbtTag): T?

    }

}
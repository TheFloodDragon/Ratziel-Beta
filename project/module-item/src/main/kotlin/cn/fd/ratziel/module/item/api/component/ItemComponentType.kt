package cn.fd.ratziel.module.item.api.component

import cn.fd.ratziel.module.item.api.component.transformer.JsonTransformer
import cn.fd.ratziel.module.item.api.component.transformer.MinecraftTransformer
import cn.fd.ratziel.module.item.api.component.transformer.NbtTransformer
import kotlinx.serialization.KSerializer

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
    val id: String

    /**
     * 组件封装对象类型
     */
    val type: Class<T>

    /**
     * 组件封装对象的序列化器
     */
    val serializer: KSerializer<T>

    /**
     * 组件转换模块接口
     */
    val transforming: Transforming<T>

    /**
     * 是否支持当前版本
     */
    val isSupported: Boolean

    /**
     * 组件转换模块接口。
     *
     * 对外统一暴露 JSON、NBT 与 Minecraft 三套转换能力：
     * - [jsonTransformer] 始终可用
     * - [nbtTransformer] 始终直接面向物品根 NBT 进行读、写、删
     * - [minecraftTransformer] 始终可用；旧版本可由 legacy adapter 提供默认实现
     */
    interface Transforming<T> {

        /**
         * 获取 [JsonTransformer]。
         */
        val jsonTransformer: JsonTransformer<T>

        /**
         * 获取 [NbtTransformer]。
         *
         * 该转换器直接面向物品根 NBT 进行读、写、删操作。
         */
        val nbtTransformer: NbtTransformer<T>

        /**
         * 获取 [MinecraftTransformer]。
         *
         * 旧版本可由 legacy adapter 基于 [NbtTransformer] 提供兼容实现。
         */
        val minecraftTransformer: MinecraftTransformer<T>

    }

}
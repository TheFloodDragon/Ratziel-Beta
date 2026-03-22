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
     * 组件转换模块接口。
     *
     * 不同 Minecraft 版本下可用的底层组件体系不同：
     * - 1.20.5 以下通常使用 [NbtTransformer]
     * - 1.20.5 及以上通常使用 [MinecraftTransformer]
     *
     * 对当前版本不适用的转换器访问时，允许实现抛出明确异常。
     */
    interface Transforming<T> {

        /**
         * 获取 [JsonTransformer]
         */
        val jsonTransformer: JsonTransformer<T>

        /**
         * 获取 [NbtTransformer]。
         *
         * 当当前版本不再使用 NBT 组件体系时，访问可能抛出异常。
         */
        val nbtTransformer: NbtTransformer<T>

        /**
         * 获取 [MinecraftTransformer]。
         *
         * 当当前版本尚未支持 Minecraft 原生组件体系时，访问可能抛出异常。
         */
        val minecraftTransformer: MinecraftTransformer<T>

    }

}
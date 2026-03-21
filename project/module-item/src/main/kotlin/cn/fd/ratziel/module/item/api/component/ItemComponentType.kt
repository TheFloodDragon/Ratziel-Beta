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
     * 组件转换模块接口
     */
    interface Transforming<T> {

        /**
         * 获取 [JsonTransformer]
         */
        val jsonTransformer: JsonTransformer<T>

        /**
         * 获取 [NbtTransformer]
         */
        val nbtTransformer: NbtTransformer<T>

        /**
         * 获取 [MinecraftTransformer]
         */
        val minecraftTransformer: MinecraftTransformer<T>

    }

}
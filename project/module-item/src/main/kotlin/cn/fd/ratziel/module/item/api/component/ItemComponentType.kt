package cn.fd.ratziel.module.item.api.component

import cn.fd.ratziel.core.Identifier
import kotlinx.serialization.KSerializer

/**
 * ItemComponentType - 物品组件类型
 * 
 * @author TheFloodDragon
 * @since 2025/11/29 21:48
 */
interface ItemComponentType<T : Any> {

    /**
     * 物品组件标识符
     *   1.20.5 + 的格式为 minecraft:custom_data (NamespacedIdentifier)
     *   1.20.5- 的格式为 display.Name (NbtNodeIdentifier)
     */
    val identifier: Identifier

    /**
     * 物品组件序列化器
     */
    val serializer: KSerializer<T>

    /**
     * 数据类型转换器
     */
    val transformer: Transformer<T>

    /**
     * Transformer
     */
    interface Transformer<T : Any> {

        fun transform(src: Any): T

        fun detransform(tar: T): Any

        class NoTransformation<T : Any> : Transformer<T> {
            @Suppress("UNCHECKED_CAST")
            override fun transform(src: Any) = src as T
            override fun detransform(tar: T) = tar
        }

    }

    /**
     * Unverified
     */
    class Unverified<T : Any>(
        override val identifier: Identifier,
        override val serializer: KSerializer<T>,
        override val transformer: Transformer<T> = Transformer.NoTransformation(),
    ) : ItemComponentType<T> {
        override fun toString() = "UnverifiedComponentType(identifier=$identifier, serializer=$serializer, transformer=$transformer)"
    }

}

package cn.fd.ratziel.module.item.api.component

import cn.altawk.nbt.tag.NbtTag
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
     * Transformer - 数据类型转换器
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

    interface SerializerTransformer<T:Any> : Transformer<T>{
        override fun transform(src: Any): T
        fun transform(root: NbtTag): T
    }

    /**
     * Unverified - 未经校验的物品组件类型
     */
    class Unverified<T : Any> internal constructor(
        override val identifier: Identifier,
        override val serializer: KSerializer<T>,
        override val transformer: Transformer<T> = Transformer.NoTransformation(),
        private val verified: Boolean = true,
    ) : ItemComponentType<T> {

        constructor(identifier: Identifier, serializer: KSerializer<T>, transformer: Transformer<T>) : this(identifier, serializer, transformer, false)

        override fun toString() = (if (verified) "VerifiedComponentType" else "UnverifiedComponentType") +
                "(identifier=$identifier, serializer=$serializer, transformer=$transformer)"
    }

    /**
     * Unsupported - 当前版本不支持的组件类型
     */
    class Unsupported<T : Any> internal constructor(val key: String) : ItemComponentType<T> {
        override val identifier get() = error("UnsupportedComponentType called.")
        override val serializer get() = error("UnsupportedComponentType called.")
        override val transformer get() = error("UnsupportedComponentType called.")
        override fun toString() = "UnsupportedComponentType(key=$key)"
    }

}

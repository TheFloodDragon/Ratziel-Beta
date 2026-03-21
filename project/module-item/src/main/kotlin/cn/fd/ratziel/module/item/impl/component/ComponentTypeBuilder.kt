package cn.fd.ratziel.module.item.impl.component

import cn.altawk.nbt.NbtPath
import cn.fd.ratziel.core.exception.UnsupportedVersionException
import cn.fd.ratziel.module.item.ItemElement
import cn.fd.ratziel.module.item.api.component.ItemComponentType
import cn.fd.ratziel.module.item.api.component.transformer.*
import kotlinx.serialization.KSerializer
import taboolib.module.nms.MinecraftVersion

/**
 * ComponentTypeBuilder
 *
 * 用于组装一个 [ItemComponentType] 所需的转换器集合（JSON / NBT / Minecraft）。
 *
 * 传入组件标识符、组件类型与对应的 Kotlinx 序列化器后，
 * 可以手动指定各类转换器，也可以通过 [serialJsonEntry] / [serialNbtEntry]
 * 快速创建基于序列化器的 entry 转换器。
 *
 * 调用 [build] 前必须提供 JSON、NBT 与 Minecraft 三类转换器。
 *
 * @param T 组件数据类型
 * @property id 组件标识符（插件内部命名）
 * @property type 组件封装对象类型
 * @property serializer Kotlinx 序列化器，用于 JSON / NBT 的序列化与反序列化
 *
 * @author TheFloodDragon
 * @since 2026/1/1 22:51
 */
class ComponentTypeBuilder<T>(val id: String, val type: Class<T>, val serializer: KSerializer<T>) {

    // JSON 转换器（由 [json] 或 [serialJsonEntry] 设置）
    private var jsonTransformer: JsonTransformer<T>? = null

    // NBT 转换器（由 [nbt] 或 [serialNbtEntry] 设置）
    private var nbtTransformer: NbtTransformer<T>? = null

    // Minecraft 转换器（由 [minecraft] 设置）
    private var minecraftTransformer: MinecraftTransformer<T>? = null

    /** 当前版本的数字 ID **/
    val v get() = MinecraftVersion.versionId

    /**
     * 设置当前版本是否支持该组件类型。
     *
     * 该值为 `false` 时，构建结果仍会保留组件的基础信息，
     * 但不会提供可用的转换模块。
     */
    var isSupported = true

    /**
     * 指定 JSON 转换器。
     */
    fun json(jsonTransformer: JsonTransformer<T>) = this.apply {
        this.jsonTransformer = jsonTransformer
    }

    /**
     * 指定 NBT 转换器。
     */
    fun nbt(nbtTransformer: NbtTransformer<T>) = this.apply {
        this.nbtTransformer = nbtTransformer
    }

    /**
     * 指定 Minecraft（NMS）转换器。
     */
    fun minecraft(transformer: MinecraftTransformer<T>) = this.apply {
        this.minecraftTransformer = transformer
    }

    /**
     * 创建一个基于组件 [id] 的 JSON entry 转换器。
     *
     * 序列化时使用 [id] 作为主字段名；反序列化时除 [id] 外，
     * 也会尝试匹配 [alias] 中提供的兼容字段名。
     *
     * @param alias JSON 字段的兼容别名；若不传则仅识别 [id]
     */
    fun serialJsonEntry(vararg alias: String) = this.apply {
        this.jsonTransformer = SerialJsonTransformer.EntryTransformer(
            serializer, ItemElement.json, id, *alias
        )
    }

    /**
     * 创建一个基于指定 NBT 路径的 entry 转换器。
     *
     * 转换器会将序列化后的组件值写入 [path] 指向的位置，
     * 并在反序列化时从相同路径读取对应的 NBT 数据。
     *
     * @param path NBT 路径字符串（例如："tag.CustomData"）
     */
    fun serialNbtEntry(path: String) = this.apply {
        this.nbtTransformer = SerialNbtTransformer.EntryTransformer(
            serializer, ItemElement.nbt, NbtPath(path)
        )
    }

    /**
     * 创建一个基于 Minecraft 内部组件系统的转换器。
     *
     * 该方法会根据提供的 Minecraft 组件类型 ID 和封装对象与 Minecraft 组件对象之间的转换器，
     * 构建一个可直接读写 Minecraft 组件数据的转换模块。
     *
     * @see MinecraftHandleTransformer
     * @param key Minecraft 内部组件类型 ID（例如："minecraft:custom_name"） [net.minecraft.core.component.DataComponents]
     * @param e2mTransformer 封装对象与 Minecraft 组件对象之间的转换器
     */
    fun minecraftKeyed(key: String, e2mTransformer: MinecraftE2MTransformer<T>) = this.apply {
        if (v < 12005) return@apply // 不支持 1.20.5-
        this.minecraftTransformer = MinecraftHandleTransformer(key, e2mTransformer)
    }

    /**
     * 创建一个基于 Minecraft 内部组件系统的转换器。
     *
     * 该方法会根据提供的 Minecraft 组件类型 ID 和封装对象与 Minecraft 组件对象之间的转换器，
     * 构建一个可直接读写 Minecraft 组件数据的转换模块。
     *
     * @see MinecraftHandleTransformer
     * @param key Minecraft 内部组件类型 ID（例如："minecraft:custom_name"） [net.minecraft.core.component.DataComponents]
     * @param e2mClass 实现目标 [MinecraftE2MTransformer] 的类名
     */
    fun minecraftKeyed(key: String, e2mClass: String) = minecraftKeyed(key, e2mByReflex(e2mClass))

    /**
     * 通过反射 [cn.fd.ratziel.module.item.impl.component.transformers] 中的类获取 [MinecraftE2MTransformer]
     */
    fun e2mByReflex(className: String): MinecraftE2MTransformer<T> {
        return MinecraftE2MTransformer.of("cn.fd.ratziel.module.item.impl.component.transformers.$className")
    }

    /**
     * 构建 [ItemComponentType]。
     *
     * 当前实现会先校验三类转换器都已提供，随后再根据 [isSupported]
     * 决定是否返回带有转换模块的组件类型。
     */
    fun build(): ItemComponentType<T> {
        // 校验构建所需的转换器
        requireNotNull(jsonTransformer) { "JsonTransformer is required for component '$id'" }
        requireNotNull(nbtTransformer) { "NbtTransformer is required for component '$id'" }
        require(v < 12005 || minecraftTransformer != null) { "MinecraftTransformer is required for component '$id'" }

        // 当前版本不支持该组件
        if (!isSupported) {
            return InternalComponentType(id, type, serializer, null)
        }

        val transforming = DelegateTransforming(
            jsonTransformer!!,
            nbtTransformer!!,
            if (v >= 12005) minecraftTransformer else null
        )
        return InternalComponentType(id, type, serializer, transforming)
    }

    /**
     * [ItemComponentType.Transforming] 的简单委托实现。
     */
    private class DelegateTransforming<T>(
        override val jsonTransformer: JsonTransformer<T>,
        override val nbtTransformer: NbtTransformer<T>,
        private val _minecraftTransformer: MinecraftTransformer<T>?,
    ) : ItemComponentType.Transforming<T> {
        override val minecraftTransformer: MinecraftTransformer<T>
            get() = _minecraftTransformer ?: throw UnsupportedVersionException("MinecraftTransformer only supports Minecraft 1.20.5+ !")

        override fun toString() =
            "Transforming(json=$jsonTransformer, nbt=$nbtTransformer, minecraft=${this@DelegateTransforming.minecraftTransformer})"
    }

    /**
     * [ItemComponentType] 的内部实现。
     */
    private class InternalComponentType<T>(
        override val id: String,
        override val type: Class<T>,
        override val serializer: KSerializer<T>,
        /** 转换模块；当前版本不支持该组件时为 null。 **/
        private val _transforming: ItemComponentType.Transforming<T>?,
    ) : ItemComponentType<T> {
        override val transforming: ItemComponentType.Transforming<T>
            get() = _transforming
                ?: throw UnsupportedVersionException("Component '$id' is not supported in Minecraft ${MinecraftVersion.minecraftVersion}")

        override fun toString() = "ItemComponentType(id=$id, type=$type, serializer=$serializer, transforming=$transforming)"
    }

}
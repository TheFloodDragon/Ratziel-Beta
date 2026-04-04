package cn.fd.ratziel.module.item.impl.component

import cn.altawk.nbt.NbtPath
import cn.fd.ratziel.core.exception.UnsupportedVersionException
import cn.fd.ratziel.module.item.ItemElement
import cn.fd.ratziel.module.item.api.component.ItemComponentType
import cn.fd.ratziel.module.item.api.component.transformer.*
import kotlinx.serialization.KSerializer
import taboolib.common.io.runningClassMapWithoutLibrary
import taboolib.module.nms.AsmClassTranslation
import taboolib.module.nms.MinecraftVersion

/**
 * ComponentTypeBuilder
 *
 * 用于组装一个 [ItemComponentType] 所需的转换器集合。
 *
 * 传入组件标识符、组件类型与对应的 Kotlinx 序列化器后，
 * 可以手动指定各类转换器，也可以通过 [serialJsonEntry] / [serialNbtEntry]
 * 快速创建基于序列化器的 entry 转换器。
 *
 * 对于受支持组件，调用 [build] 前必须始终提供 JSON 与 NBT 转换器；
 * 另外 Minecraft 转换器遵循以下策略：
 * - 1.20.5 以下若未显式提供，会自动回填 legacy adapter
 * - 1.20.5 及以上必须显式提供真实的 Minecraft 转换器
 *
 * @param T 组件数据类型
 * @property id 组件标识符（插件内部命名）
 * @property type 组件封装对象类型
 * @property serializer Kotlinx 序列化器，用于 JSON / NBT 的序列化与反序列化
 *
 * @author TheFloodDragon
 * @since 2026/1/1 22:51
 */
class ComponentTypeBuilder<T>(
    val id: String,
    val type: Class<T>,
    val serializer: KSerializer<T>,
) {

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
     *
     * 转换器应直接基于物品根 NBT 实现组件的写入、读取与删除。
     */
    fun nbt(nbtTransformer: NbtTransformer<T>) = this.apply {
        this.nbtTransformer = nbtTransformer
    }

    /**
     * 指定 Minecraft（NMS）转换器。
     *
     * 在 1.20.5+ 通常应传入真实的 Minecraft 组件转换器；
     * 在旧版本中若不提供，则会在 [build] 时自动回填 legacy adapter。
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
     * 创建一个基于指定 NBT 路径的序列化转换器。
     *
     * 转换器会直接对物品根 NBT 的 [path] 位置执行写入、读取与删除，
     * 适合将单个组件映射到某个固定子路径下。
     *
     * @param path NBT 路径字符串（例如："tag.CustomData"）
     */
    fun serialNbtEntry(path: String) = this.apply {
        this.nbtTransformer = SerialNbtTransformer(
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
    fun minecraftKeyed(key: String, e2mTransformer: () -> MinecraftE2MTransformer<T>) = this.apply {
        if (v < 12005) return@apply // 不支持 1.20.5-
        this.minecraftTransformer = MinecraftHandleTransformer(key, e2mTransformer())
    }

    /**
     * 构建 [ItemComponentType]。
     *
     * 对于受支持组件：
     * - [JsonTransformer] 必须提供
     * - [NbtTransformer] 必须提供
     * - [MinecraftTransformer] 在 1.20.5+ 必须显式提供；旧版本可自动回填 legacy adapter
     */
    fun build(): ItemComponentType<T> {
        if (!isSupported) {
            return InternalComponentType(id, type, serializer, null)
        }

        val jsonTransformer = requireNotNull(jsonTransformer) {
            "JsonTransformer is required for component '$id'"
        }
        val nbtTransformer = requireNotNull(nbtTransformer) {
            "NbtTransformer is required for component '$id'"
        }
        val minecraftTransformer = minecraftTransformer ?: if (v < 12005) {
            LegacyMinecraftTransformer(id, nbtTransformer)
        } else {
            throw IllegalArgumentException(
                "MinecraftTransformer is required for component '$id' on Minecraft 1.20.5+"
            )
        }

        val transforming = DelegateTransforming(
            jsonTransformer = jsonTransformer,
            nbtTransformer = nbtTransformer,
            minecraftTransformer = minecraftTransformer,
        )
        return InternalComponentType(id, type, serializer, transforming)
    }

    /**
     * [ItemComponentType.Transforming] 的简单委托实现。
     */
    private class DelegateTransforming<T>(
        override val jsonTransformer: JsonTransformer<T>,
        override val nbtTransformer: NbtTransformer<T>,
        override val minecraftTransformer: MinecraftTransformer<T>,
    ) : ItemComponentType.Transforming<T> {
        override fun toString() = "Transforming(json=$jsonTransformer, nbt=$nbtTransformer, minecraft=$minecraftTransformer)"
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

        override fun toString() = "ItemComponentType(id='$id', tserializer=$serializer, transforming=$transforming)"
    }

    companion object {

        private val proxyInstanceMap = mutableMapOf<String, Any>()

        /**
         * 代理 [cn.fd.ratziel.module.item.impl.component.transformers] 中的类
         */
        @JvmStatic
        fun <T : Any> proxyClass(className: String): T {
            return nmsProxyClass("cn.fd.ratziel.module.item.impl.component.transformers.$className")
        }

        /**
         * 通过全限定类名反射构造任意对象实例。
         */
        @JvmStatic
        fun <T : Any> nmsProxyClass(fullName: String): T {
            if (proxyInstanceMap.containsKey(fullName)) {
                @Suppress("UNCHECKED_CAST")
                return proxyInstanceMap[fullName] as T
            }
            fun createInstance(clazz: Class<*>): Any {
                val constructor = clazz.declaredConstructors.find {
                    it.parameterTypes.size == 0
                }
                if (constructor != null) {
                    constructor.isAccessible = true
                    return constructor.newInstance()
                }
                throw NoSuchMethodException("No empty constructor found: ${clazz.name}")
            }

            val proxyClass = AsmClassTranslation(fullName).createNewClass()
            runningClassMapWithoutLibrary.filter { (name, _) -> name.startsWith("$fullName$") }.forEach { (name, _) ->
                AsmClassTranslation(name).createNewClass()
            }
            @Suppress("UNCHECKED_CAST")
            val newInstance = createInstance(proxyClass) as T
            proxyInstanceMap[fullName] = newInstance
            return newInstance
        }

    }

}

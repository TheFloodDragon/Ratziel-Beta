package cn.fd.ratziel.module.item.impl.component

import cn.altawk.nbt.NbtPath
import cn.fd.ratziel.module.item.ItemElement
import cn.fd.ratziel.module.item.api.component.ItemComponentType
import cn.fd.ratziel.module.item.api.component.transformer.DelegateTransformer
import cn.fd.ratziel.module.item.api.component.transformer.MinecraftComponentTransformer
import cn.fd.ratziel.module.item.api.component.transformer.SerialJsonTransformer
import cn.fd.ratziel.module.item.api.component.transformer.SerialNbtTransformer
import kotlinx.serialization.KSerializer

/**
 * TransformerBuilder
 * 
 * 构建器，用于方便地为某个属性 key 创建一组 Item 组件转换器（JSON/NBT/可选的 NMS）。
 *
 * 通过传入属性的 key 与对应的 Kotlinx 序列化器（`KSerializer<T>`)，
 * 可以按需指定 JSON 或 NBT 的 entry 级别转换器，若未指定则使用序列化的默认 transformer。
 *
 * @param T 转换的数据类型
 * @property key 属性的键名，用于在 JSON/NBT 中定位值
 * @property serializer Kotlinx 序列化器，用于在序列化/反序列化时转换对象
 *
 * @author TheFloodDragon
 * @since 2026/1/1 22:51
 */
class TransformerBuilder<T>(val key: String, val serializer: KSerializer<T>) {

    // 可选的 JSON 转换器（entry 形式或整体形式）
    private var jsonTransformer: ItemComponentType.JsonTransformer<T>? = null

    // 可选的 NBT 转换器（entry 形式或整体形式）
    private var nbtTransformer: ItemComponentType.NbtTransformer<T>? = null

    // 预留的 NMS 转换器（可通过 [nms] 方法设置）
    @Suppress("unused")
    private var nmsTransformer: MinecraftComponentTransformer<T>? = null

    /**
     * 为该 key 注册一个基于 JSON entry 的转换器，同时可指定别名（alias）用于匹配 JSON 中的多个字段名。
     *
     * @param alias 可变参数，表示该属性在 JSON 中可能的别名；若不传则仅使用 key 本身
     * @return this 以支持链式调用
     */
    fun jsonEntry(vararg alias: String) = this.apply {
        this.jsonTransformer = SerialJsonTransformer.EntryTransformer(
            serializer, ItemElement.json, key, *alias
        )
    }

    /**
     * 为该 key 注册一个基于 NBT path 的 entry 转换器，使用给定的 NBT 路径定位值。
     *
     * @param path NBT 路径字符串（例如："tag.CustomData"）
     * @return this 以支持链式调用
     */
    fun nbtEntry(path: String) = this.apply {
        this.nbtTransformer = SerialNbtTransformer.EntryTransformer(
            serializer, ItemElement.nbt, NbtPath(path)
        )
    }

    /**
     * 与 [nbtEntry(path: String)] 等价，但路径由 `ItemSheet.mapping(key)` 自动推断。
     * 便于默认映射场景下的简化调用。
     *
     * @return this 以支持链式调用
     */
    fun nbtEntry() = this.nbtEntry(ItemSheet.mapping(key))

    /**
     * 注册一个用于 NMS（Minecraft 内部实现）的组件转换器。该转换器在当前版本中并未在
     * [build] 的返回值中被直接使用，但预留此方法以便将来在 NMS 相关流程中使用。
     *
     * @param transformer 要注册的 NMS 转换器
     * @return this 以支持链式调用
     */
    fun nms(transformer: MinecraftComponentTransformer<T>) = this.apply {
        this.nmsTransformer = transformer
    }

    /**
     * 将已配好的 JSON/NBT 转换器组装成最终的 [ItemComponentType.Transformer]。
     *
     * 若某一侧（JSON 或 NBT）未显式配置，则使用基于序列化器的默认实现：
     * - JSON: [SerialJsonTransformer]
     * - NBT: [SerialNbtTransformer]
     *
     * 返回值通常是一个 [DelegateTransformer]，它在读写时会委派到对应的 JSON/NBT 转换器上。
     *
     * 注意：当前实现中返回的 [DelegateTransformer] 不包含 NMS 转换器的委派；若未来需要，可在
     * 返回类型或构建逻辑中扩展以支持 NMS。
     *
     * @return 组合后的组件转换器，用于注册到组件类型中
     */
    fun build(): ItemComponentType.Transformer<T> {
        // 显式使用 nmsTransformer（无实际逻辑），以便编译器不报未使用字段的警告
        @Suppress("ControlFlowWithEmptyBody")
        if (this.nmsTransformer != null) {
            // reserved for future use
        }
        val jsonTransformer = this.jsonTransformer ?: SerialJsonTransformer(serializer, ItemElement.json)
        val nbtTransformer = this.nbtTransformer ?: SerialNbtTransformer(serializer, ItemElement.nbt)
        return DelegateTransformer(jsonTransformer, nbtTransformer)
    }

}
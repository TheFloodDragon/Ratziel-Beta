package cn.fd.ratziel.module.item.api.component.transformer

import cn.altawk.nbt.NbtFormat
import cn.altawk.nbt.NbtPath
import cn.altawk.nbt.tag.NbtCompound
import cn.altawk.nbt.tag.NbtTag
import cn.fd.ratziel.module.nbt.delete
import cn.fd.ratziel.module.nbt.read
import cn.fd.ratziel.module.nbt.write
import kotlinx.serialization.KSerializer

/**
 * SerialNbtTransformer
 *
 * 基于 Kotlinx Serialization 的 [NbtTransformer] 默认实现。
 *
 * 默认行为：
 * - [write] 要求序列化结果本身就是一个 [NbtCompound]，并将其合并到根节点
 * - [read] 直接从根节点反序列化
 * - [remove] 默认不支持，因为无法可靠判断应删除哪些根节点字段
 *
 * 若组件只占用根 NBT 下的某个固定路径，请优先使用 [EntryTransformer]。
 *
 * @author TheFloodDragon
 * @since 2026/1/1 22:15
 */
open class SerialNbtTransformer<T>(
    val serializer: KSerializer<T>,
    val nbtFormat: NbtFormat,
) : NbtTransformer<T> {

    /**
     * 将组件编码为原始 [NbtTag]。
     */
    protected open fun encode(component: T): NbtTag {
        return nbtFormat.encodeToNbtTag(serializer, component)
    }

    /**
     * 从原始 [NbtTag] 解码组件。
     */
    protected open fun decode(tag: NbtTag): T? {
        return nbtFormat.decodeFromNbtTag(serializer, tag)
    }

    override fun write(root: NbtCompound, component: T) {
        val serialized = encode(component)
        require(serialized is NbtCompound) {
            "SerialNbtTransformer(serializer=$serializer) can only write NbtCompound to a root compound. " +
                "Use EntryTransformer or a custom NbtTransformer for non-compound payloads."
        }
        root.merge(serialized, true)
    }

    override fun read(root: NbtCompound): T? {
        return decode(root)
    }

    override fun remove(root: NbtCompound) {
        throw UnsupportedOperationException(
            "SerialNbtTransformer(serializer=$serializer) does not know how to remove data from the root compound. " +
                "Use EntryTransformer or a custom NbtTransformer."
        )
    }

    override fun toString() = "SerialNbtTransformer(serializer=$serializer)"

    /**
     * EntryTransformer
     *
     * 将组件映射到根 NBT 下的指定路径，并提供对应的写入、读取与删除能力。
     *
     * @author TheFloodDragon
     * @since 2026/1/1 21:41
     */
    open class EntryTransformer<T>(
        /** 序列化器 **/
        serializer: KSerializer<T>,
        /** Nbt 格式 **/
        nbtFormat: NbtFormat,
        /** Nbt 路径 **/
        val path: NbtPath,
    ) : SerialNbtTransformer<T>(serializer, nbtFormat) {

        override fun write(root: NbtCompound, component: T) {
            root.write(path, encode(component), true)
        }

        override fun read(root: NbtCompound): T? {
            val tag = root.read(path, false) ?: return null
            return decode(tag)
        }

        override fun remove(root: NbtCompound) {
            root.delete(path)
        }

        override fun toString() = "SerialNbtEntryTransformer(path=$path)"

    }

}

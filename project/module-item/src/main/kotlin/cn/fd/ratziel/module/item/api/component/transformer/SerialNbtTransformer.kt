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
 * 基于 Kotlinx Serialization 的 [NbtTransformer] 路径映射实现。
 *
 * 组件会被序列化为一个 [NbtTag]，并写入到根 NBT 的指定 [path]；
 * 读取和删除也都围绕该路径进行。
 *
 * @author TheFloodDragon
 * @since 2026/1/1 22:15
 */
open class SerialNbtTransformer<T>(
    val serializer: KSerializer<T>,
    val nbtFormat: NbtFormat,
    val path: NbtPath,
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

    override fun writeTo(root: NbtCompound, component: T) {
        root.write(path, encode(component), true)
    }

    override fun readFrom(root: NbtCompound): T? {
        val tag = root.read(path, false) ?: return null
        return decode(tag)
    }

    override fun removeFrom(root: NbtCompound) {
        root.delete(path)
    }

    override fun toString() = "SerialNbtTransformer(path=$path, serializer=$serializer)"

}

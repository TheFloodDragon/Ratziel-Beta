package cn.fd.ratziel.module.item.api.component.transformer

import cn.altawk.nbt.NbtFormat
import cn.altawk.nbt.NbtPath
import cn.altawk.nbt.tag.NbtCompound
import cn.altawk.nbt.tag.NbtTag
import cn.fd.ratziel.module.nbt.read
import cn.fd.ratziel.module.nbt.write
import kotlinx.serialization.KSerializer

/**
 * SerialNbtTransformer
 * 
 * @author TheFloodDragon
 * @since 2026/1/1 22:15
 */
open class SerialNbtTransformer<T>(
    val serializer: KSerializer<T>,
    val nbtFormat: NbtFormat,
) : NbtTransformer<T> {

    override fun toNbtTag(component: T, root: NbtCompound): NbtTag {
        return nbtFormat.encodeToNbtTag(serializer, component)
    }

    override fun fromNbtTag(tag: NbtTag): T? {
        return nbtFormat.decodeFromNbtTag(serializer, tag)
    }

    override fun toString() = "SerialNbtTransformer(serializer=$serializer, nbtFormat=$nbtFormat)"

    /**
     * EntryTransformer
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

        override fun toNbtTag(component: T, root: NbtCompound): NbtCompound {
            val serialized = super.toNbtTag(component, root)
            return NbtCompound { write(path, serialized, true) }
        }

        override fun fromNbtTag(tag: NbtTag): T? {
            if (tag !is NbtCompound) return null
            val tag = tag.read(path, false) ?: return null
            return super.fromNbtTag(tag)
        }

        override fun toString() = "SerialNbtEntryTransformer(path=$path, serializer=$serializer, nbtFormat=$nbtFormat)"

    }

}
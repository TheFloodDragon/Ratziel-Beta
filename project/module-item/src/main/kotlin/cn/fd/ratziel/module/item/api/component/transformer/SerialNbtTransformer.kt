package cn.fd.ratziel.module.item.api.component.transformer

import cn.altawk.nbt.NbtFormat
import cn.altawk.nbt.NbtPath
import cn.altawk.nbt.tag.NbtCompound
import cn.altawk.nbt.tag.NbtTag
import cn.fd.ratziel.module.item.api.component.ItemComponentType
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
) : ItemComponentType.NbtTransformer<T> {

    override fun transformToNbtTag(tar: T): NbtTag {
        return nbtFormat.encodeToNbtTag(serializer, tar)
    }

    override fun detransformFromNbtTag(src: NbtTag): T? {
        return nbtFormat.decodeFromNbtTag(serializer, src)
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

        override fun transformToNbtTag(tar: T): NbtCompound {
            val serialized = super.transformToNbtTag(tar)
            return NbtCompound { write(path, serialized, true) }
        }

        override fun detransformFromNbtTag(src: NbtTag): T? {
            if (src !is NbtCompound) return null
            val tag = src.read(path, false) ?: return null
            return super.detransformFromNbtTag(tag)
        }

        override fun toString() = "SerialNbtEntryTransformer(path=$path, serializer=$serializer, nbtFormat=$nbtFormat)"

    }

}
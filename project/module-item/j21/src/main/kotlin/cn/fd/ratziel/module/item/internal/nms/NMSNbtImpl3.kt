package cn.fd.ratziel.module.item.internal.nms

import cn.altawk.nbt.tag.*
import cn.fd.ratziel.core.exception.UnsupportedTypeException

/**
 * NMSItemImpl3 - 1.21.5+ (Mojang你是真出)
 *
 * @author TheFloodDragon
 * @since 2025/8/4 10:44
 */
@Suppress("unused", "DuplicatedCode")
class NMSNbtImpl3 : NMSNbt {

    override fun toNms(data: NbtTag): NbtBase = when (data) {
        is NbtString -> NBTTagString.valueOf(data.content)
        is NbtInt -> NBTTagInt.valueOf(data.content)
        is NbtByte -> NBTTagByte.valueOf(data.content)
        is NbtDouble -> NBTTagDouble.valueOf(data.content)
        is NbtFloat -> NBTTagFloat.valueOf(data.content)
        is NbtLong -> NBTTagLong.valueOf(data.content)
        is NbtShort -> NBTTagShort.valueOf(data.content)
        is NbtIntArray -> NBTTagIntArray(data.content.copyOf())
        is NbtByteArray -> NBTTagByteArray(data.content.copyOf())
        is NbtLongArray -> NBTTagLongArray(data.content.copyOf())
        is NbtList -> NBTTagList().apply { data.forEach { add(toNms(it)) } }
        is NbtCompound -> NBTTagCompound().apply { data.forEach { put(it.key, toNms(it.value)) } }
    }

    override fun fromNms(nmsData: Any): NbtTag = when (nmsData) {
        // Primitive 类型的都改 record 类了
        is NBTTagString -> NbtString(nmsData.value)
        is NBTTagInt -> NbtInt(nmsData.value)
        is NBTTagByte -> NbtByte(nmsData.value)
        is NBTTagDouble -> NbtDouble(nmsData.value)
        is NBTTagFloat -> NbtFloat(nmsData.value)
        is NBTTagLong -> NbtLong(nmsData.value)
        is NBTTagShort -> NbtShort(nmsData.value)
        is NBTTagByteArray -> NbtByteArray(nmsData.asByteArray.copyOf())
        is NBTTagIntArray -> NbtIntArray(nmsData.asIntArray.copyOf())
        is NBTTagLongArray -> NbtLongArray(nmsData.asLongArray.copyOf())
        is NBTTagList -> NbtList { nmsData.forEach { add(fromNms(it)) } }
        // 1.20.5 - allKeys
        // 1.21.5 - keySet()
        // Mojang 你很幽默??
        is NBTTagCompound -> NbtCompound { nmsData.entrySet().forEach { put(it.key, fromNms(it.value)) } }
        else -> throw UnsupportedTypeException(nmsData::class.java)
    }

}

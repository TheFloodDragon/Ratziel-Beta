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

    override fun toNms(input: NbtTag): NbtBase = when (input) {
        is NbtString -> NBTTagString.valueOf(input.content)
        is NbtInt -> NBTTagInt.valueOf(input.content)
        is NbtByte -> NBTTagByte.valueOf(input.content)
        is NbtDouble -> NBTTagDouble.valueOf(input.content)
        is NbtFloat -> NBTTagFloat.valueOf(input.content)
        is NbtLong -> NBTTagLong.valueOf(input.content)
        is NbtShort -> NBTTagShort.valueOf(input.content)
        is NbtIntArray -> NBTTagIntArray(input.content.copyOf())
        is NbtByteArray -> NBTTagByteArray(input.content.copyOf())
        is NbtLongArray -> NBTTagLongArray(input.content.copyOf())
        is NbtList -> NBTTagList().apply { input.forEach { add(toNms(it)) } }
        is NbtCompound -> NBTTagCompound().apply { input.forEach { put(it.key, toNms(it.value)) } }
    }

    override fun fromNms(input: Any): NbtTag = when (input) {
        // Primitive 类型的都改 record 类了
        is NBTTagString -> NbtString(input.value)
        is NBTTagInt -> NbtInt(input.value)
        is NBTTagByte -> NbtByte(input.value)
        is NBTTagDouble -> NbtDouble(input.value)
        is NBTTagFloat -> NbtFloat(input.value)
        is NBTTagLong -> NbtLong(input.value)
        is NBTTagShort -> NbtShort(input.value)
        is NBTTagByteArray -> NbtByteArray(input.asByteArray.copyOf())
        is NBTTagIntArray -> NbtIntArray(input.asIntArray.copyOf())
        is NBTTagLongArray -> NbtLongArray(input.asLongArray.copyOf())
        is NBTTagList -> NbtList { input.forEach { add(fromNms(it)) } }
        // 1.20.5 - allKeys
        // 1.21.5 - keySet()
        // Mojang 你很幽默??
        is NBTTagCompound -> NbtCompound { input.entrySet().forEach { put(it.key, fromNms(it.value)) } }
        else -> throw UnsupportedTypeException(input::class.java)
    }

}

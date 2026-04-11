package cn.fd.ratziel.module.item.internal.nms

import cn.altawk.nbt.tag.*
import cn.fd.ratziel.core.exception.UnsupportedTypeException
import net.minecraft.nbt.*

/**
 * NMSItemImpl3 - 1.21.5+ (Mojang你是真出)
 *
 * @author TheFloodDragon
 * @since 2025/8/4 10:44
 */
@Suppress("unused", "DuplicatedCode")
class NMSNbtImpl3 : NMSNbt {

    override fun toNms(input: NbtTag): Tag = when (input) {
        is NbtString -> StringTag.valueOf(input.content)
        is NbtInt -> IntTag.valueOf(input.content)
        is NbtByte -> ByteTag.valueOf(input.content)
        is NbtDouble -> DoubleTag.valueOf(input.content)
        is NbtFloat -> FloatTag.valueOf(input.content)
        is NbtLong -> LongTag.valueOf(input.content)
        is NbtShort -> ShortTag.valueOf(input.content)
        is NbtIntArray -> IntArrayTag(input.content.copyOf())
        is NbtByteArray -> ByteArrayTag(input.content.copyOf())
        is NbtLongArray -> LongArrayTag(input.content.copyOf())
        is NbtList -> ListTag().apply { input.forEach { add(toNms(it)) } }
        is NbtCompound -> CompoundTag().apply { input.forEach { put(it.key, toNms(it.value)) } }
    }

    override fun fromNms(input: Any): NbtTag = when (input) {
        // Primitive 类型的都改 record 类了
        is StringTag -> NbtString(input.value)
        is IntTag -> NbtInt(input.value)
        is ByteTag -> NbtByte(input.value)
        is DoubleTag -> NbtDouble(input.value)
        is FloatTag -> NbtFloat(input.value)
        is LongTag -> NbtLong(input.value)
        is ShortTag -> NbtShort(input.value)
        is ByteArrayTag -> NbtByteArray(input.asByteArray.copyOf())
        is IntArrayTag -> NbtIntArray(input.asIntArray.copyOf())
        is LongArrayTag -> NbtLongArray(input.asLongArray.copyOf())
        is ListTag -> NbtList { input.forEach { add(fromNms(it)) } }
        // 1.20.5 - allKeys
        // 1.21.5 - keySet()
        // Mojang 你很幽默??
        is CompoundTag -> NbtCompound { input.entrySet().forEach { put(it.key, fromNms(it.value)) } }
        else -> throw UnsupportedTypeException(input::class.java)
    }

}

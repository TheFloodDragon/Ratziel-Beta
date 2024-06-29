@file:Suppress("ConvertObjectToDataObject")

package cn.fd.ratziel.module.item.nbt

import cn.fd.ratziel.core.exception.UnsupportedTypeException
import cn.fd.ratziel.module.item.nbt.NBTType.*
import taboolib.library.reflex.ClassConstructor
import taboolib.library.reflex.ClassField
import taboolib.library.reflex.ReflexClass
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.nmsClass

/**
 * NMSUtil
 *
 * @author TheFloodDragon
 * @since 2024/3/15 18:56
 */
internal sealed class NMSUtil {

    abstract val nmsClass: Class<*>

    abstract val constructor: ClassConstructor

    abstract val sourceField: ClassField

    val reflexClass by lazy { ReflexClass.of(nmsClass, false) }

    fun isOwnClass(clazz: Class<*>): Boolean = nmsClass.isAssignableFrom(clazz)

    object NtCompound : NMSUtil() {
        /**
         * net.minecraft.nbt.NBTTagCompound
         */
        override val nmsClass by lazy { nmsClass("NBTTagCompound") }

        /**
         * protected NBTTagCompound(Map<String, NBTBase> var0)
         * public NBTTagCompound() { this(Maps.newHashMap()); }
         */
        override val constructor by lazy { reflexClass.structure.getConstructorByType(Map::class.java) }

        /**
         * private final Map<String, NBTBase> x
         */
        override val sourceField by lazy { reflexClass.structure.getField(if (MinecraftVersion.isUniversal) "x" else "map") }

        /**
         * public CompoundTag h()
         */
        val methodClone by lazy { reflexClass.structure.getMethod(if (MinecraftVersion.isUniversal) "h" else "clone") }

    }

    object NtList : NMSUtil() {
        /**
         * net.minecraft.nbt.NBTTagList
         */
        override val nmsClass by lazy { nmsClass("NBTTagList") }

        /**
         * NBTTagList(List<NBTBase> var0, byte var1)
         * public NBTTagList() { this(Lists.newArrayList(), (byte)0); }
         */
        override val constructor by lazy { reflexClass.structure.getConstructorByType(List::class.java, Byte::class.java) }

        /**
         *  private final List<NBTBase> c
         */
        override val sourceField by lazy { reflexClass.structure.getField(if (MinecraftVersion.isUniversal) "c" else "list") }

        /**
         * public ListTag e()
         */
        val methodClone by lazy { reflexClass.structure.getMethod(if (MinecraftVersion.isUniversal) "e" else "clone") }

    }

    object NtString : NMSUtil() {
        /**
         * net.minecraft.nbt.NBTTagString
         */
        override val nmsClass by lazy { nmsClass("NBTTagString") }

        /**
         * private NBTTagString(String var0)
         */
        override val constructor by lazy { reflexClass.structure.getConstructorByType(String::class.java) }

        /**
         *  private final String A
         */
        override val sourceField by lazy { reflexClass.structure.getField(if (MinecraftVersion.isUniversal) "A" else "data") }

    }

    object NtByte : NMSUtil() {
        /**
         * net.minecraft.nbt.NBTTagByte
         */
        override val nmsClass by lazy { nmsClass("NBTTagByte") }

        /**
         * NBTTagByte(byte var0)
         */
        override val constructor by lazy { reflexClass.structure.getConstructorByType(Byte::class.java) }

        /**
         *  private final byte x
         */
        override val sourceField by lazy { reflexClass.structure.getField(if (MinecraftVersion.isUniversal) "x" else "data") }

    }

    object NtByteArray : NMSUtil() {
        /**
         * net.minecraft.nbt.NBTTagByteArray
         */
        override val nmsClass by lazy { nmsClass("NBTTagByteArray") }

        /**
         * public NBTTagByteArray(byte[] bytes)
         */
        override val constructor by lazy { reflexClass.structure.getConstructorByType(ByteArray::class.java) }

        /**
         * private byte[] c
         */
        override val sourceField by lazy { reflexClass.structure.getField(if (MinecraftVersion.isUniversal) "c" else "data") }

    }

    object NtDouble : NMSUtil() {
        /**
         * net.minecraft.nbt.NBTTagDouble
         */
        override val nmsClass by lazy { nmsClass("NBTTagDouble") }

        /**
         * private NBTTagDouble(double var0)
         */
        override val constructor by lazy { reflexClass.structure.getConstructorByType(Double::class.java) }

        /**
         * private final double w
         */
        override val sourceField by lazy { reflexClass.structure.getField(if (MinecraftVersion.isUniversal) "w" else "data") }

    }

    object NtFloat : NMSUtil() {
        /**
         * net.minecraft.nbt.NBTTagFloat
         */
        override val nmsClass by lazy { nmsClass("NBTTagFloat") }

        /**
         * private NBTTagFloat(float var0)
         */
        override val constructor by lazy { reflexClass.structure.getConstructorByType(Float::class.java) }

        /**
         * private final float w
         */
        override val sourceField by lazy { reflexClass.structure.getField(if (MinecraftVersion.isUniversal) "w" else "data") }

    }

    object NtInt : NMSUtil() {
        /**
         * net.minecraft.nbt.NBTTagInt
         */
        override val nmsClass by lazy { nmsClass("NBTTagInt") }

        /**
         * public static NBTTagInt a(int var0)
         */
        override val constructor by lazy { reflexClass.structure.getConstructorByType(Int::class.java) }

        /**
         * private final int c
         */
        override val sourceField by lazy { reflexClass.structure.getField(if (MinecraftVersion.isUniversal) "c" else "data") }

    }

    object NtIntArray : NMSUtil() {
        /**
         * net.minecraft.nbt.NBTTagIntArray
         */
        override val nmsClass by lazy { nmsClass("NBTTagIntArray") }

        /**
         * public NBTTagIntArray(int[] var0)
         */
        override val constructor by lazy { reflexClass.structure.getConstructorByType(IntArray::class.java) }

        /**
         * private int[] c
         */
        override val sourceField by lazy { reflexClass.structure.getField(if (MinecraftVersion.isUniversal) "c" else "data") }

    }

    object NtLong : NMSUtil() {
        /**
         * net.minecraft.nbt.NBTTagLong
         */
        override val nmsClass by lazy { nmsClass("NBTTagLong") }

        /**
         * NBTTagLong(long var0)
         */
        override val constructor by lazy { reflexClass.structure.getConstructorByType(Long::class.java) }

        /**
         * private final long c
         */
        override val sourceField by lazy { reflexClass.structure.getField(if (MinecraftVersion.isUniversal) "c" else "data") }

    }

    object NtLongArray : NMSUtil() {
        /**
         * net.minecraft.nbt.NBTTagLongArray
         */
        override val nmsClass by lazy { nmsClass("NBTTagLongArray") }

        /**
         * public NBTTagLongArray(long[] var0)
         */
        override val constructor by lazy { reflexClass.structure.getConstructorByType(LongArray::class.java) }

        /**
         * private long[] c
         */
        override val sourceField by lazy { reflexClass.structure.getField(if (MinecraftVersion.isUniversal) "c" else "b") }

    }

    object NtShort : NMSUtil() {

        /**
         * net.minecraft.nbt.NBTTagShort
         */
        override val nmsClass by lazy { nmsClass("NBTTagShort") }

        /**
         * NBTTagShort(short var0)
         */
        override val constructor by lazy { reflexClass.structure.getConstructorByType(Short::class.java) }

        /**
         * private final short c
         */
        override val sourceField by lazy { reflexClass.structure.getField(if (MinecraftVersion.isUniversal) "c" else "data") }

    }

    object NtBase {
        /**
         * net.minecraft.nbt.NBTBase
         */
        val nmsClass by lazy { nmsClass("NBTBase") }

    }

    companion object {

        internal fun inferUtil(type: NBTType): NMSUtil = when (type) {
            BYTE -> NtByte
            SHORT -> NtShort
            INT -> NtInt
            LONG -> NtLong
            FLOAT -> NtFloat
            DOUBLE -> NtDouble
            BYTE_ARRAY -> NtByteArray
            STRING -> NtString
            LIST -> NtList
            COMPOUND -> NtCompound
            INT_ARRAY -> NtIntArray
            LONG_ARRAY -> NtLongArray
            else -> throw UnsupportedTypeException(type)
        }

    }

}
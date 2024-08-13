@file:Suppress("ConvertObjectToDataObject")

package cn.fd.ratziel.module.item.nbt

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
internal sealed class NMSUtil(val type: NBTType) {

    abstract val nmsClass: Class<*>

    abstract val constructor: ClassConstructor

    abstract val sourceField: ClassField

    val reflexClass by lazy { ReflexClass.of(nmsClass, false) }

    val structure get() = reflexClass.structure

    fun isOwnClass(clazz: Class<*>): Boolean = nmsClass.isAssignableFrom(clazz)

    object NtCompound : NMSUtil(NBTType.COMPOUND) {
        /**
         * net.minecraft.nbt.NBTTagCompound
         */
        override val nmsClass by lazy { nmsClass("NBTTagCompound") }

        /**
         * protected NBTTagCompound(Map<String, NBTBase> var0)
         * public NBTTagCompound() { this(Maps.newHashMap()); }
         */
        override val constructor by lazy { structure.getConstructorByType(Map::class.java) }

        /**
         * private final Map<String, NBTBase> x
         */
        override val sourceField by lazy { structure.getFieldSilently("tags") ?: structure.getField(if (MinecraftVersion.isUniversal) "x" else "map") }

        /**
         * public CompoundTag h()
         */
        val methodClone by lazy { structure.getMethodSilently("copy") ?: structure.getMethod(if (MinecraftVersion.isUniversal) "h" else "clone") }

    }

    object NtList : NMSUtil(NBTType.LIST) {
        /**
         * net.minecraft.nbt.NBTTagList
         */
        override val nmsClass by lazy { nmsClass("NBTTagList") }

        /**
         * NBTTagList(List<NBTBase> var0, byte var1)
         * public NBTTagList() { this(Lists.newArrayList(), (byte)0); }
         */
        override val constructor by lazy { structure.getConstructorByType(List::class.java, Byte::class.java) }

        /**
         *  private final List<NBTBase> c
         */
        override val sourceField by lazy { structure.getFieldSilently("list") ?: structure.getField(if (MinecraftVersion.isUniversal) "c" else "list") }

        /**
         * public ListTag e()
         */
        val methodClone by lazy { structure.getMethodSilently("copy") ?: structure.getMethod(if (MinecraftVersion.isUniversal) "e" else "clone") }

    }

    object NtString : NMSUtil(NBTType.STRING) {
        /**
         * net.minecraft.nbt.NBTTagString
         */
        override val nmsClass by lazy { nmsClass("NBTTagString") }

        /**
         * private NBTTagString(String var0)
         */
        override val constructor by lazy { structure.getConstructorByType(String::class.java) }

        /**
         *  private final String A
         */
        override val sourceField by lazy { structure.getFieldSilently("data") ?: structure.getField(if (MinecraftVersion.isUniversal) "A" else "data") }

    }

    object NtByte : NMSUtil(NBTType.BYTE) {
        /**
         * net.minecraft.nbt.NBTTagByte
         */
        override val nmsClass by lazy { nmsClass("NBTTagByte") }

        /**
         * NBTTagByte(byte var0)
         */
        override val constructor by lazy { structure.getConstructorByType(Byte::class.java) }

        /**
         *  private final byte x
         */
        override val sourceField by lazy { structure.getFieldSilently("data") ?: structure.getField(if (MinecraftVersion.isUniversal) "x" else "data") }

    }

    object NtByteArray : NMSUtil(NBTType.BYTE_ARRAY) {
        /**
         * net.minecraft.nbt.NBTTagByteArray
         */
        override val nmsClass by lazy { nmsClass("NBTTagByteArray") }

        /**
         * public NBTTagByteArray(byte[] bytes)
         */
        override val constructor by lazy { structure.getConstructorByType(ByteArray::class.java) }

        /**
         * private byte[] c
         */
        override val sourceField by lazy { structure.getFieldSilently("data") ?: structure.getField(if (MinecraftVersion.isUniversal) "c" else "data") }

    }

    object NtDouble : NMSUtil(NBTType.DOUBLE) {
        /**
         * net.minecraft.nbt.NBTTagDouble
         */
        override val nmsClass by lazy { nmsClass("NBTTagDouble") }

        /**
         * private NBTTagDouble(double var0)
         */
        override val constructor by lazy { structure.getConstructorByType(Double::class.java) }

        /**
         * private final double w
         */
        override val sourceField by lazy { structure.getFieldSilently("data") ?: structure.getField(if (MinecraftVersion.isUniversal) "w" else "data") }

    }

    object NtFloat : NMSUtil(NBTType.FLOAT) {
        /**
         * net.minecraft.nbt.NBTTagFloat
         */
        override val nmsClass by lazy { nmsClass("NBTTagFloat") }

        /**
         * private NBTTagFloat(float var0)
         */
        override val constructor by lazy { structure.getConstructorByType(Float::class.java) }

        /**
         * private final float w
         */
        override val sourceField by lazy { structure.getFieldSilently("data") ?: structure.getField(if (MinecraftVersion.isUniversal) "w" else "data") }

    }

    object NtInt : NMSUtil(NBTType.INT) {
        /**
         * net.minecraft.nbt.NBTTagInt
         */
        override val nmsClass by lazy { nmsClass("NBTTagInt") }

        /**
         * public static NBTTagInt a(int var0)
         */
        override val constructor by lazy { structure.getConstructorByType(Int::class.java) }

        /**
         * private final int c
         */
        override val sourceField by lazy { structure.getFieldSilently("data") ?: structure.getField(if (MinecraftVersion.isUniversal) "c" else "data") }

    }

    object NtIntArray : NMSUtil(NBTType.INT_ARRAY) {
        /**
         * net.minecraft.nbt.NBTTagIntArray
         */
        override val nmsClass by lazy { nmsClass("NBTTagIntArray") }

        /**
         * public NBTTagIntArray(int[] var0)
         */
        override val constructor by lazy { structure.getConstructorByType(IntArray::class.java) }

        /**
         * private int[] c
         */
        override val sourceField by lazy { structure.getFieldSilently("data") ?: structure.getField(if (MinecraftVersion.isUniversal) "c" else "data") }

    }

    object NtLong : NMSUtil(NBTType.LONG) {
        /**
         * net.minecraft.nbt.NBTTagLong
         */
        override val nmsClass by lazy { nmsClass("NBTTagLong") }

        /**
         * NBTTagLong(long var0)
         */
        override val constructor by lazy { structure.getConstructorByType(Long::class.java) }

        /**
         * private final long c
         */
        override val sourceField by lazy { structure.getFieldSilently("data") ?: structure.getField(if (MinecraftVersion.isUniversal) "c" else "data") }

    }

    object NtLongArray : NMSUtil(NBTType.LONG_ARRAY) {
        /**
         * net.minecraft.nbt.NBTTagLongArray
         */
        override val nmsClass by lazy { nmsClass("NBTTagLongArray") }

        /**
         * public NBTTagLongArray(long[] var0)
         */
        override val constructor by lazy { structure.getConstructorByType(LongArray::class.java) }

        /**
         * private long[] c
         */
        override val sourceField by lazy { structure.getFieldSilently("data") ?: structure.getField(if (MinecraftVersion.isUniversal) "c" else "b") }

    }

    object NtShort : NMSUtil(NBTType.SHORT) {

        /**
         * net.minecraft.nbt.NBTTagShort
         */
        override val nmsClass by lazy { nmsClass("NBTTagShort") }

        /**
         * NBTTagShort(short var0)
         */
        override val constructor by lazy { structure.getConstructorByType(Short::class.java) }

        /**
         * private final short c
         */
        override val sourceField by lazy { structure.getFieldSilently("data") ?: structure.getField(if (MinecraftVersion.isUniversal) "c" else "data") }

    }

    object NtBase {

        /**
         * net.minecraft.nbt.NBTBase
         */
        val nmsClass by lazy { nmsClass("NBTBase") }

    }

}
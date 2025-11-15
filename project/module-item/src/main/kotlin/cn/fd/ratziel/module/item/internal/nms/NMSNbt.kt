package cn.fd.ratziel.module.item.internal.nms

import cn.altawk.nbt.tag.*
import cn.fd.ratziel.core.exception.UnsupportedTypeException
import taboolib.library.reflex.UnsafeAccess
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.nmsProxy
import java.lang.invoke.MethodHandle

/**
 * NMSUtil
 *
 * @author TheFloodDragon
 * @since 2024/9/1 12:41
 */
interface NMSNbt {

    /**
     * [NbtTag] to [NBTTagCompound]
     */
    fun toNms(input: NbtTag): Any

    /**
     * [NBTTagCompound] to [NbtTag]
     */
    fun fromNms(input: Any): NbtTag

    companion object {

        @JvmStatic
        val INSTANCE by lazy {
            if (MinecraftVersion.versionId >= 12105) {
                nmsProxy<NMSNbt>("{name}Impl3")
            } else if (MinecraftVersion.versionId >= 12005) {
                nmsProxy<NMSNbt>("{name}Impl2")
            } else nmsProxy<NMSNbt>("{name}Impl1")
        }

    }

}

/**
 * 1.20.5+
 */
@Suppress("unused", "DuplicatedCode")
class NMSNbtImpl2 : NMSNbt {

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
        is NBTTagString -> NbtString(input.asString)
        is NBTTagInt -> NbtInt(input.asInt)
        is NBTTagByte -> NbtByte(input.asByte)
        is NBTTagDouble -> NbtDouble(input.asDouble)
        is NBTTagFloat -> NbtFloat(input.asFloat)
        is NBTTagLong -> NbtLong(input.asLong)
        is NBTTagShort -> NbtShort(input.asShort)
        is NBTTagByteArray -> NbtByteArray(input.asByteArray.copyOf())
        is NBTTagIntArray -> NbtIntArray(input.asIntArray.copyOf())
        is NBTTagLongArray -> NbtLongArray(input.asLongArray.copyOf())
        is NBTTagList -> NbtList { input.forEach { add(fromNms(it)) } }
        is NBTTagCompound -> NbtCompound { input.allKeys.forEach { put(it, fromNms(input.get(it)!!)) } }
        else -> throw UnsupportedTypeException(input::class.java)
    }

}

/**
 * 1.20.4-
 *
 * 代码参考自: Taboolib/nms-tag-legacy
 */
@Suppress("unused")
class NMSNbtImpl1 : NMSNbt {

    private val nbtTagCompoundGetter = getter(NBTTagCompound12::class.java, if (MinecraftVersion.isUniversal) "x" else "map")
    private val nbtTagListGetter = getter(NBTTagList12::class.java, if (MinecraftVersion.isUniversal) "c" else "list")
    private val nbtTagListTypeSetter = setter(NBTTagList12::class.java, if (MinecraftVersion.isUniversal) "w" else "type")
    private val nbtTagByteGetter = getter(NBTTagByte12::class.java, if (MinecraftVersion.isUniversal) "x" else "data")
    private val nbtTagShortGetter = getter(NBTTagShort12::class.java, if (MinecraftVersion.isUniversal) "c" else "data")
    private val nbtTagIntGetter = getter(NBTTagInt12::class.java, if (MinecraftVersion.isUniversal) "c" else "data")
    private val nbtTagLongGetter = getter(NBTTagLong12::class.java, if (MinecraftVersion.isUniversal) "c" else "data")
    private val nbtTagFloatGetter = getter(NBTTagFloat12::class.java, if (MinecraftVersion.isUniversal) "w" else "data")
    private val nbtTagDoubleGetter = getter(NBTTagDouble12::class.java, if (MinecraftVersion.isUniversal) "w" else "data")
    private val nbtTagStringGetter = getter(NBTTagString12::class.java, if (MinecraftVersion.isUniversal) "A" else "data")
    private val nbtTagByteArrayGetter = getter(NBTTagByteArray12::class.java, if (MinecraftVersion.isUniversal) "c" else "data")
    private val nbtTagIntArrayGetter = getter(NBTTagIntArray12::class.java, if (MinecraftVersion.isUniversal) "c" else "data")
    private val nbtTagLongArrayGetter = if (MinecraftVersion.isHigherOrEqual(MinecraftVersion.V1_12)) {
        getter(NBTTagLongArray12::class.java, if (MinecraftVersion.isUniversal) "c" else "b")
    } else null

    private val new = MinecraftVersion.isHigherOrEqual(MinecraftVersion.V1_15)

    override fun toNms(input: NbtTag): Any = when (input) {
        is NbtString -> if (new) NBTTagString15.a(input.content) else NBTTagString12(input.content)
        is NbtInt -> if (new) NBTTagInt15.a(input.content) else NBTTagInt12(input.content)
        is NbtByte -> if (new) NBTTagByte15.a(input.content) else NBTTagByte12(input.content)
        is NbtDouble -> if (new) NBTTagDouble15.a(input.content) else NBTTagDouble12(input.content)
        is NbtFloat -> if (new) NBTTagFloat15.a(input.content) else NBTTagFloat12(input.content)
        is NbtLong -> if (new) NBTTagLong15.a(input.content) else NBTTagLong12(input.content)
        is NbtShort -> if (new) NBTTagShort15.a(input.content) else NBTTagShort12(input.content)
        is NbtIntArray -> NBTTagIntArray12(input.content.copyOf())
        is NbtByteArray -> NBTTagByteArray12(input.content.copyOf())
        is NbtLongArray -> NBTTagLongArray12(input.content.copyOf())
        is NbtList -> NBTTagList12().also { src ->
            // 反射获取字段：
            // private final List<NbtBase> list;
            val list = nbtTagListGetter.get<MutableList<Any>>(src)
            val dataList = input.content
            if (dataList.isNotEmpty()) {
                dataList.forEach { list.add(toNms(it)) }
                // 修改 NBTTagList 的类型，不改他妈这条 List 作废，天坑。。。
                nbtTagListTypeSetter.set(src, dataList.first().type.id)
            }
        }

        is NbtCompound -> NBTTagCompound().also { src ->
            // 反射获取字段：
            // private final Map<String, NbtBase> map
            val map = nbtTagCompoundGetter.get<MutableMap<String, Any>>(src)
            input.forEach { map[it.key] = toNms(it.value) }
        }
    }

    override fun fromNms(input: Any): NbtTag = when (input) {
        is NBTTagString12 -> NbtString(nbtTagStringGetter.get(input))
        is NBTTagInt12 -> NbtInt(nbtTagIntGetter.get(input))
        is NBTTagByte12 -> NbtByte(nbtTagByteGetter.get<Byte>(input))
        is NBTTagDouble12 -> NbtDouble(nbtTagDoubleGetter.get(input))
        is NBTTagFloat12 -> NbtFloat(nbtTagFloatGetter.get(input))
        is NBTTagLong12 -> NbtLong(nbtTagLongGetter.get(input))
        is NBTTagShort12 -> NbtShort(nbtTagShortGetter.get(input))
        is NBTTagByteArray12 -> NbtByteArray(nbtTagByteArrayGetter.get<ByteArray>(input).copyOf())
        is NBTTagIntArray12 -> NbtIntArray(nbtTagIntArrayGetter.get<IntArray>(input).copyOf())
        is NBTTagLongArray12 -> NbtLongArray(nbtTagLongArrayGetter!!.get<LongArray>(input).copyOf())
        is NBTTagList12 -> NbtList().apply { nbtTagListGetter.get<List<Any>>(input).forEach { add(fromNms(it)) } }
        is NBTTagCompound12 -> NbtCompound().apply { nbtTagCompoundGetter.get<Map<String, Any>>(input).forEach { put(it.key, fromNms(it.value)) } }
        else -> throw UnsupportedOperationException("NmsNBTTag cannot convert to NBTTag: $input")
    }

    private fun getter(type: Class<*>, name: String): MethodHandle {
        return UnsafeAccess.lookup.unreflectGetter(type.getDeclaredField(name).apply { isAccessible = true })
    }

    private fun setter(type: Class<*>, name: String): MethodHandle {
        return UnsafeAccess.lookup.unreflectSetter(type.getDeclaredField(name).apply { isAccessible = true })
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> MethodHandle.get(src: Any): T = bindTo(src).invoke() as T

    private fun <T> MethodHandle.set(src: Any, value: T) = bindTo(src).invoke(value)

}

typealias NBTTagCompound12 = net.minecraft.server.v1_12_R1.NBTTagCompound
typealias NBTTagList12 = net.minecraft.server.v1_12_R1.NBTTagList
typealias NBTTagByte12 = net.minecraft.server.v1_12_R1.NBTTagByte
typealias NBTTagShort12 = net.minecraft.server.v1_12_R1.NBTTagShort
typealias NBTTagInt12 = net.minecraft.server.v1_12_R1.NBTTagInt
typealias NBTTagLong12 = net.minecraft.server.v1_12_R1.NBTTagLong
typealias NBTTagFloat12 = net.minecraft.server.v1_12_R1.NBTTagFloat
typealias NBTTagDouble12 = net.minecraft.server.v1_12_R1.NBTTagDouble
typealias NBTTagString12 = net.minecraft.server.v1_12_R1.NBTTagString
typealias NBTTagByteArray12 = net.minecraft.server.v1_12_R1.NBTTagByteArray
typealias NBTTagIntArray12 = net.minecraft.server.v1_12_R1.NBTTagIntArray
typealias NBTTagLongArray12 = net.minecraft.server.v1_12_R1.NBTTagLongArray

typealias NBTTagByte15 = net.minecraft.server.v1_15_R1.NBTTagByte
typealias NBTTagShort15 = net.minecraft.server.v1_15_R1.NBTTagShort
typealias NBTTagInt15 = net.minecraft.server.v1_15_R1.NBTTagInt
typealias NBTTagLong15 = net.minecraft.server.v1_15_R1.NBTTagLong
typealias NBTTagFloat15 = net.minecraft.server.v1_15_R1.NBTTagFloat
typealias NBTTagDouble15 = net.minecraft.server.v1_15_R1.NBTTagDouble
typealias NBTTagString15 = net.minecraft.server.v1_15_R1.NBTTagString

typealias NbtBase = net.minecraft.nbt.NBTBase
typealias NBTTagCompound = net.minecraft.nbt.NBTTagCompound
typealias NBTTagList = net.minecraft.nbt.NBTTagList
typealias NBTTagByte = net.minecraft.nbt.NBTTagByte
typealias NBTTagShort = net.minecraft.nbt.NBTTagShort
typealias NBTTagInt = net.minecraft.nbt.NBTTagInt
typealias NBTTagLong = net.minecraft.nbt.NBTTagLong
typealias NBTTagFloat = net.minecraft.nbt.NBTTagFloat
typealias NBTTagDouble = net.minecraft.nbt.NBTTagDouble
typealias NBTTagString = net.minecraft.nbt.NBTTagString
typealias NBTTagByteArray = net.minecraft.nbt.NBTTagByteArray
typealias NBTTagIntArray = net.minecraft.nbt.NBTTagIntArray
typealias NBTTagLongArray = net.minecraft.nbt.NBTTagLongArray
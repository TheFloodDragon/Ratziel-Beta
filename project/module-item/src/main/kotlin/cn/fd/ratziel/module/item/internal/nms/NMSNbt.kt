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
    fun toNms(data: NbtTag): Any

    /**
     * [NBTTagCompound] to [NbtTag]
     */
    fun fromNms(nmsData: Any): NbtTag

    companion object {

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
@Suppress("unused")
class NMSNbtImpl2 : NMSNbt {

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
        is NBTTagString -> NbtString(nmsData.asString)
        is NBTTagInt -> NbtInt(nmsData.asInt)
        is NBTTagByte -> NbtByte(nmsData.asByte)
        is NBTTagDouble -> NbtDouble(nmsData.asDouble)
        is NBTTagFloat -> NbtFloat(nmsData.asFloat)
        is NBTTagLong -> NbtLong(nmsData.asLong)
        is NBTTagShort -> NbtShort(nmsData.asShort)
        is NBTTagByteArray -> NbtByteArray(nmsData.asByteArray.copyOf())
        is NBTTagIntArray -> NbtIntArray(nmsData.asIntArray.copyOf())
        is NBTTagLongArray -> NbtLongArray(nmsData.asLongArray.copyOf())
        is NBTTagList -> NbtList { nmsData.forEach { add(fromNms(it)) } }
        is NBTTagCompound -> NbtCompound { nmsData.allKeys.forEach { put(it, fromNms(nmsData.get(it)!!)) } }
        else -> throw UnsupportedTypeException(nmsData::class.java)
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

    override fun toNms(data: NbtTag): Any = when (data) {
        is NbtString -> if (new) NBTTagString15.a(data.content) else NBTTagString12(data.content)
        is NbtInt -> if (new) NBTTagInt15.a(data.content) else NBTTagInt12(data.content)
        is NbtByte -> if (new) NBTTagByte15.a(data.content) else NBTTagByte12(data.content)
        is NbtDouble -> if (new) NBTTagDouble15.a(data.content) else NBTTagDouble12(data.content)
        is NbtFloat -> if (new) NBTTagFloat15.a(data.content) else NBTTagFloat12(data.content)
        is NbtLong -> if (new) NBTTagLong15.a(data.content) else NBTTagLong12(data.content)
        is NbtShort -> if (new) NBTTagShort15.a(data.content) else NBTTagShort12(data.content)
        is NbtIntArray -> NBTTagIntArray12(data.content.copyOf())
        is NbtByteArray -> NBTTagByteArray12(data.content.copyOf())
        is NbtLongArray -> NBTTagLongArray12(data.content.copyOf())
        is NbtList -> NBTTagList12().also { src ->
            // 反射获取字段：
            // private final List<NbtBase> list;
            val list = nbtTagListGetter.get<MutableList<Any>>(src)
            val dataList = data.content
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
            data.forEach { map[it.key] = toNms(it.value) }
        }
    }

    override fun fromNms(nmsData: Any): NbtTag = when (nmsData) {
        is NBTTagString12 -> NbtString(nbtTagStringGetter.get(nmsData))
        is NBTTagInt12 -> NbtInt(nbtTagIntGetter.get(nmsData))
        is NBTTagByte12 -> NbtByte(nbtTagByteGetter.get<Byte>(nmsData))
        is NBTTagDouble12 -> NbtDouble(nbtTagDoubleGetter.get(nmsData))
        is NBTTagFloat12 -> NbtFloat(nbtTagFloatGetter.get(nmsData))
        is NBTTagLong12 -> NbtLong(nbtTagLongGetter.get(nmsData))
        is NBTTagShort12 -> NbtShort(nbtTagShortGetter.get(nmsData))
        is NBTTagByteArray12 -> NbtByteArray(nbtTagByteArrayGetter.get<ByteArray>(nmsData).copyOf())
        is NBTTagIntArray12 -> NbtIntArray(nbtTagIntArrayGetter.get<IntArray>(nmsData).copyOf())
        is NBTTagLongArray12 -> NbtLongArray(nbtTagLongArrayGetter!!.get<LongArray>(nmsData).copyOf())
        is NBTTagList12 -> NbtList().apply { nbtTagListGetter.get<List<Any>>(nmsData).forEach { add(fromNms(it)) } }
        is NBTTagCompound12 -> NbtCompound().apply { nbtTagCompoundGetter.get<Map<String, Any>>(nmsData).forEach { put(it.key, fromNms(it.value)) } }
        else -> throw UnsupportedOperationException("NmsNBTTag cannot convert to NBTTag: $nmsData")
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
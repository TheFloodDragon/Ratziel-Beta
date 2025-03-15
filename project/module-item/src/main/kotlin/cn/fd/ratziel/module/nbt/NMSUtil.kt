package cn.fd.ratziel.module.nbt

import cn.altawk.nbt.tag.*
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
interface NMSUtil {

    /**
     * 判断类型
     */
    fun inferType(nmsData: Any): NbtType

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
            if (MinecraftVersion.versionId >= 12005)
                nmsProxy<NMSUtil>("{name}Impl2")
            else nmsProxy<NMSUtil>("{name}Impl1")
        }

    }

}

/**
 * 1.20.5+
 */
@Suppress("unused")
class NMSUtilImpl2 : NMSUtil {

    override fun inferType(nmsData: Any): NbtType {
        val nmsId = (nmsData as NbtBase).id
        return NbtType.of(nmsId)!!
    }

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
        else -> throw UnsupportedOperationException("NBTTag cannot convert to NmsNBTTag: $data")
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
        is NBTTagList -> NbtList().apply { nmsData.forEach { add(fromNms(it)) } }
        is NBTTagCompound -> NbtCompound().apply { nmsData.allKeys.forEach { put(it, fromNms(nmsData.get(it)!!)) } }
        else -> throw UnsupportedOperationException("NmsNBTTag cannot convert to NBTTag: $nmsData")
    }

}

/**
 * 1.20.4-
 *
 * 代码参考自: Taboolib/nms-tag-legacy
 */
@Suppress("unused")
class NMSUtilImpl1 : NMSUtil {

    val nbtTagCompoundGetter = unreflectGetter(NBTTagCompound12::class.java, if (MinecraftVersion.isUniversal) "x" else "map")
    val nbtTagListGetter = unreflectGetter(NBTTagList12::class.java, if (MinecraftVersion.isUniversal) "c" else "list")
    val nbtTagListTypeSetter = unreflectSetter(NBTTagList12::class.java, if (MinecraftVersion.isUniversal) "w" else "type")
    val nbtTagByteGetter = unreflectGetter(NBTTagByte12::class.java, if (MinecraftVersion.isUniversal) "x" else "data")
    val nbtTagShortGetter = unreflectGetter(NBTTagShort12::class.java, if (MinecraftVersion.isUniversal) "c" else "data")
    val nbtTagIntGetter = unreflectGetter(NBTTagInt12::class.java, if (MinecraftVersion.isUniversal) "c" else "data")
    val nbtTagLongGetter = unreflectGetter(NBTTagLong12::class.java, if (MinecraftVersion.isUniversal) "c" else "data")
    val nbtTagFloatGetter = unreflectGetter(NBTTagFloat12::class.java, if (MinecraftVersion.isUniversal) "w" else "data")
    val nbtTagDoubleGetter = unreflectGetter(NBTTagDouble12::class.java, if (MinecraftVersion.isUniversal) "w" else "data")
    val nbtTagStringGetter = unreflectGetter(NBTTagString12::class.java, if (MinecraftVersion.isUniversal) "A" else "data")
    val nbtTagByteArrayGetter = unreflectGetter(NBTTagByteArray12::class.java, if (MinecraftVersion.isUniversal) "c" else "data")
    val nbtTagIntArrayGetter = unreflectGetter(NBTTagIntArray12::class.java, if (MinecraftVersion.isUniversal) "c" else "data")
    val nbtTagLongArrayGetter = if (MinecraftVersion.isHigherOrEqual(MinecraftVersion.V1_12)) {
        unreflectGetter(NBTTagLongArray12::class.java, if (MinecraftVersion.isUniversal) "c" else "b")
    } else null

    val new = MinecraftVersion.isHigherOrEqual(MinecraftVersion.V1_15)

    override fun inferType(nmsData: Any): NbtType {
        val nmsId = (nmsData as NbtBase12).typeId
        return NbtType.entries.find { it.id == nmsId }!!
    }

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
                nbtTagListTypeSetter.set(src, dataList.first().type.id.toByte())
            }
        }

        is NbtCompound -> NBTTagCompound().also { src ->
            // 反射获取字段：
            // private final Map<String, NbtBase> map
            val map = nbtTagCompoundGetter.get<MutableMap<String, Any>>(src)
            data.forEach { map[it.key] = toNms(it.value) }
        }

        else -> throw UnsupportedOperationException("NBTTag cannot convert to NmsNBTTag: $data")
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

    private fun unreflectGetter(type: Class<*>, name: String): MethodHandle {
        return UnsafeAccess.lookup.unreflectGetter(type.getDeclaredField(name).apply { isAccessible = true })
    }

    private fun unreflectSetter(type: Class<*>, name: String): MethodHandle {
        return UnsafeAccess.lookup.unreflectSetter(type.getDeclaredField(name).apply { isAccessible = true })
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> MethodHandle.get(src: Any): T = bindTo(src).invoke() as T

    private fun <T> MethodHandle.set(src: Any, value: T) = bindTo(src).invoke(value)

}

typealias NbtBase12 = net.minecraft.server.v1_12_R1.NBTBase
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
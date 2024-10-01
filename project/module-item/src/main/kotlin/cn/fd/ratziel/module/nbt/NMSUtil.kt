package cn.fd.ratziel.module.nbt

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
    fun inferType(nmsData: Any): NBTType

    /**
     * [NBTData] to [NBTTagCompound]
     */
    fun toNms(data: NBTData): Any

    /**
     * [NBTTagCompound] to [NBTData]
     */
    fun fromNms(nmsData: Any): NBTData

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

    override fun inferType(nmsData: Any): NBTType {
        val nmsId = (nmsData as NBTBase).id
        return NBTType.entries.find { it.id == nmsId }!!
    }

    override fun toNms(data: NBTData): NBTBase = when (data) {
        is NBTString -> NBTTagString.valueOf(data.content)
        is NBTInt -> NBTTagInt.valueOf(data.content)
        is NBTByte -> NBTTagByte.valueOf(data.content)
        is NBTDouble -> NBTTagDouble.valueOf(data.content)
        is NBTFloat -> NBTTagFloat.valueOf(data.content)
        is NBTLong -> NBTTagLong.valueOf(data.content)
        is NBTShort -> NBTTagShort.valueOf(data.content)
        is NBTIntArray -> NBTTagIntArray(data.content.copyOf())
        is NBTByteArray -> NBTTagByteArray(data.content.copyOf())
        is NBTLongArray -> NBTTagLongArray(data.content.copyOf())
        is NBTList -> NBTTagList().apply { data.forEach { add(toNms(it)) } }
        is NBTCompound -> NBTTagCompound().apply { data.forEach { put(it.key, toNms(it.value)) } }
        else -> throw UnsupportedOperationException("NBTData cannot convert to NmsNBTData: $data")
    }

    override fun fromNms(nmsData: Any): NBTData = when (nmsData) {
        is NBTTagString -> NBTString(nmsData.asString)
        is NBTTagInt -> NBTInt(nmsData.asInt)
        is NBTTagByte -> NBTByte(nmsData.asByte)
        is NBTTagDouble -> NBTDouble(nmsData.asDouble)
        is NBTTagFloat -> NBTFloat(nmsData.asFloat)
        is NBTTagLong -> NBTLong(nmsData.asLong)
        is NBTTagShort -> NBTShort(nmsData.asShort)
        is NBTTagByteArray -> NBTByteArray(nmsData.asByteArray.copyOf())
        is NBTTagIntArray -> NBTIntArray(nmsData.asIntArray.copyOf())
        is NBTTagLongArray -> NBTLongArray(nmsData.asLongArray.copyOf())
        is NBTTagList -> NBTList().apply { nmsData.forEach { add(fromNms(it)) } }
        is NBTTagCompound -> NBTCompound().apply { nmsData.allKeys.forEach { put(it, fromNms(nmsData.get(it)!!)) } }
        else -> throw UnsupportedOperationException("NmsNBTData cannot convert to NBTData: $nmsData")
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

    override fun inferType(nmsData: Any): NBTType {
        val nmsId = (nmsData as NBTBase12).typeId
        return NBTType.entries.find { it.id == nmsId }!!
    }

    override fun toNms(data: NBTData): Any = when (data) {
        is NBTString -> if (new) NBTTagString15.a(data.content) else NBTTagString12(data.content)
        is NBTInt -> if (new) NBTTagInt15.a(data.content) else NBTTagInt12(data.content)
        is NBTByte -> if (new) NBTTagByte15.a(data.content) else NBTTagByte12(data.content)
        is NBTDouble -> if (new) NBTTagDouble15.a(data.content) else NBTTagDouble12(data.content)
        is NBTFloat -> if (new) NBTTagFloat15.a(data.content) else NBTTagFloat12(data.content)
        is NBTLong -> if (new) NBTTagLong15.a(data.content) else NBTTagLong12(data.content)
        is NBTShort -> if (new) NBTTagShort15.a(data.content) else NBTTagShort12(data.content)
        is NBTIntArray -> NBTTagIntArray12(data.content.copyOf())
        is NBTByteArray -> NBTTagByteArray12(data.content.copyOf())
        is NBTLongArray -> NBTTagLongArray12(data.content.copyOf())
        is NBTList -> NBTTagList12().also { src ->
            // 反射获取字段：
            // private final List<NBTBase> list;
            val list = nbtTagListGetter.get<MutableList<Any>>(src)
            val dataList = data.content
            if (dataList.isNotEmpty()) {
                dataList.forEach { list.add(toNms(it)) }
                // 修改 NBTTagList 的类型，不改他妈这条 List 作废，天坑。。。
                nbtTagListTypeSetter.set(src, dataList.first().type.id.toByte())
            }
        }

        is NBTCompound -> NBTTagCompound().also { src ->
            // 反射获取字段：
            // private final Map<String, NBTBase> map
            val map = nbtTagCompoundGetter.get<MutableMap<String, Any>>(src)
            data.forEach { map[it.key] = toNms(it.value) }
        }

        else -> throw UnsupportedOperationException("NBTData cannot convert to NmsNBTData: $data")
    }

    override fun fromNms(nmsData: Any): NBTData = when (nmsData) {
        is NBTTagString12 -> NBTString(nbtTagStringGetter.get(nmsData))
        is NBTTagInt12 -> NBTInt(nbtTagIntGetter.get(nmsData))
        is NBTTagByte12 -> NBTByte(nbtTagByteGetter.get<Byte>(nmsData))
        is NBTTagDouble12 -> NBTDouble(nbtTagDoubleGetter.get(nmsData))
        is NBTTagFloat12 -> NBTFloat(nbtTagFloatGetter.get(nmsData))
        is NBTTagLong12 -> NBTLong(nbtTagLongGetter.get(nmsData))
        is NBTTagShort12 -> NBTShort(nbtTagShortGetter.get(nmsData))
        is NBTTagByteArray12 -> NBTByteArray(nbtTagByteArrayGetter.get<ByteArray>(nmsData).copyOf())
        is NBTTagIntArray12 -> NBTIntArray(nbtTagIntArrayGetter.get<IntArray>(nmsData).copyOf())
        is NBTTagLongArray12 -> NBTLongArray(nbtTagLongArrayGetter!!.get<LongArray>(nmsData).copyOf())
        is NBTTagList12 -> NBTList().apply { nbtTagListGetter.get<List<Any>>(nmsData).forEach { add(fromNms(it)) } }
        is NBTTagCompound12 -> NBTCompound().apply { nbtTagCompoundGetter.get<Map<String, Any>>(nmsData).forEach { put(it.key, fromNms(it.value)) } }
        else -> throw UnsupportedOperationException("NmsNBTData cannot convert to NBTData: $nmsData")
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

typealias NBTBase12 = net.minecraft.server.v1_12_R1.NBTBase
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

typealias NBTBase = net.minecraft.nbt.NBTBase
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
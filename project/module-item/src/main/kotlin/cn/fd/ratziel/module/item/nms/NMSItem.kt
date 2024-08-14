package cn.fd.ratziel.module.item.nms

import cn.fd.ratziel.module.item.nbt.*
import cn.fd.ratziel.module.item.nbt.NBTList
import net.minecraft.core.component.DataComponentMap
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.component.DataComponents
import net.minecraft.core.component.PatchedDataComponentMap
import net.minecraft.nbt.*
import net.minecraft.world.item.component.CustomData
import taboolib.library.reflex.ReflexClass
import taboolib.library.reflex.UnsafeAccess
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.nmsProxy
import java.lang.invoke.MethodHandle
import net.minecraft.world.item.ItemStack as NMSItemStack

/**
 * NMSItem
 *
 * @author TheFloodDragon
 * @since 2024/4/30 19:32
 */
abstract class NMSItem {

    /**
     * 获取 [NMSItemStack]的 NBT (克隆)
     * @return [NBTCompound]
     */
    abstract fun getTag(nmsItem: Any): NBTCompound?

    /**
     * 设置 [NMSItemStack]的 NBT (克隆)
     * @param tag [NBTCompound]
     */
    abstract fun setTag(nmsItem: Any, tag: NBTCompound)

    /**
     * 获取 [NMSItemStack]的自定义 NBT (克隆)
     * @return [NBTCompound]
     */
    abstract fun getCustomTag(nmsItem: Any): NBTCompound?

    /**
     * 设置 [NMSItemStack]的自定义 NBT (克隆)
     * @param tag [NBTCompound]
     */
    abstract fun setCustomTag(nmsItem: Any, tag: NBTCompound)

    /**
     * 克隆 [NMSItemStack]
     */
    abstract fun copyItem(nmsItem: Any): Any

    /**
     * [NBTData] to [NBTTagCompound]
     */
    abstract fun toNms(data: NBTData): Any

    /**
     * [NBTTagCompound] to [NBTData]
     */
    abstract fun fromNms(nmsData: Any): NBTData

    companion object {

        val INSTANCE by lazy {
            if (MinecraftVersion.majorLegacy >= 12005)
                nmsProxy<NMSItem>("{name}Impl2")
            else nmsProxy<NMSItem>("{name}Impl1")
        }

    }

}

/**
 * 1.20.5+
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class NMSItemImpl2 : NMSItem() {

    val componentsField by lazy {
        ReflexClass.of(RefItemStack.nmsClass).getField("components", remap = true)
    }

    override fun getTag(nmsItem: Any): NBTCompound? {
        val dcp = (nmsItem as NMSItemStack).componentsPatch
        return NMS12005.INSTANCE.savePatch(dcp)?.let { NBTCompound.of(it) }
    }

    override fun setTag(nmsItem: Any, tag: NBTCompound) {
        val dcp = NMS12005.INSTANCE.parsePatch(tag) as? DataComponentPatch
        val components = componentsField.get(nmsItem) as? PatchedDataComponentMap
        if (components != null) {
            components.restorePatch(dcp)
        } else {
            val newComponents = PatchedDataComponentMap(DataComponentMap.EMPTY)
            newComponents.restorePatch(dcp)
            componentsField.set(nmsItem, newComponents)
        }
    }

    override fun getCustomTag(nmsItem: Any): NBTCompound? {
        val customData = (nmsItem as NMSItemStack).get(DataComponents.CUSTOM_DATA)
        return customData?.copyTag()?.let { NBTCompound.of(it) }
    }

    override fun setCustomTag(nmsItem: Any, tag: NBTCompound) {
        val customData = CustomData.of(tag.getRaw() as NBTTagCompound)
        (nmsItem as NMSItemStack).set(DataComponents.CUSTOM_DATA, customData)
    }

    override fun copyItem(nmsItem: Any): Any {
        return (nmsItem as NMSItemStack).copy()
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
        is NBTList -> NBTTagList().also { nmsList -> data.content.forEach { nmsList.add(toNms(it)) } }
        is NBTCompound -> NBTTagCompound().also { nmsCompound -> data.content.forEach { nmsCompound.put(it.key, toNms(it.value)) } }
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
@Suppress("unused", "MemberVisibilityCanBePrivate")
class NMSItemImpl1 : NMSItem() {

    override fun getTag(nmsItem: Any): NBTCompound? {
        return nmsTagField.get(nmsItem)?.let { fromNms(it) } as? NBTCompound
    }

    override fun setTag(nmsItem: Any, tag: NBTCompound) {
        nmsTagField.set(nmsItem, toNms(tag))
    }

    override fun getCustomTag(nmsItem: Any): NBTCompound? {
        val nmsTag = nmsTagField.get(nmsItem) ?: return null
        val map = nbtTagCompoundGetter.get<MutableMap<String, Any>>(nmsTag)
        val customTag = map[ItemSheet.CUSTOM_DATA.name] as? NBTTagCompound12 ?: return null
        return fromNms(customTag) as NBTCompound
    }

    override fun setCustomTag(nmsItem: Any, tag: NBTCompound) {
        val nmsTag = nmsTagField.get(nmsItem) ?: return
        val map = nbtTagCompoundGetter.get<MutableMap<String, Any>>(nmsTag)
        map[ItemSheet.CUSTOM_DATA.name] = toNms(tag)
    }

    override fun copyItem(nmsItem: Any): Any {
        return nmsCloneMethod.invoke(nmsItem)!!
    }

    /**
     * private NBTTagCompound A
     * private NBTTagCompound tag
     */
    val nmsTagField = ReflexClass.of(RefItemStack.nmsClass).structure.getField(if (MinecraftVersion.isUniversal) "A" else "tag")

    /**
     * public nms.ItemStack p()
     * public nms.ItemStack cloneItemStack()
     * public ItemStack s()
     */
    val nmsCloneMethod by lazy {
        ReflexClass.of(RefItemStack.nmsClass).structure.getMethodByType(
            if (MinecraftVersion.majorLegacy >= 12005) "s"
            else if (MinecraftVersion.isUniversal) "p"
            else "cloneItemStack"
        )
    }

    val nbtTagCompoundGetter = unreflectGetter<NBTTagCompound12>(if (MinecraftVersion.isUniversal) "x" else "map")
    val nbtTagListGetter = unreflectGetter<NBTTagList12>(if (MinecraftVersion.isUniversal) "c" else "list")
    val nbtTagListTypeSetter = unreflectSetter<NBTTagList12>(if (MinecraftVersion.isUniversal) "w" else "type")
    val nbtTagByteGetter = unreflectGetter<NBTTagByte12>(if (MinecraftVersion.isUniversal) "x" else "data")
    val nbtTagShortGetter = unreflectGetter<NBTTagShort12>(if (MinecraftVersion.isUniversal) "c" else "data")
    val nbtTagIntGetter = unreflectGetter<NBTTagInt12>(if (MinecraftVersion.isUniversal) "c" else "data")
    val nbtTagLongGetter = unreflectGetter<NBTTagLong12>(if (MinecraftVersion.isUniversal) "c" else "data")
    val nbtTagFloatGetter = unreflectGetter<NBTTagFloat12>(if (MinecraftVersion.isUniversal) "w" else "data")
    val nbtTagDoubleGetter = unreflectGetter<NBTTagDouble12>(if (MinecraftVersion.isUniversal) "w" else "data")
    val nbtTagStringGetter = unreflectGetter<NBTTagString12>(if (MinecraftVersion.isUniversal) "A" else "data")
    val nbtTagByteArrayGetter = unreflectGetter<NBTTagByteArray12>(if (MinecraftVersion.isUniversal) "c" else "data")
    val nbtTagIntArrayGetter = unreflectGetter<NBTTagIntArray12>(if (MinecraftVersion.isUniversal) "c" else "data")
    val nbtTagLongArrayGetter =
        if (!MinecraftVersion.isHigherOrEqual(MinecraftVersion.V1_12)) null
        else unreflectGetter<NBTTagLongArray12>(if (MinecraftVersion.isUniversal) "c" else "b")

    val new = MinecraftVersion.isHigherOrEqual(MinecraftVersion.V1_15)

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
                nbtTagListTypeSetter.set(src, dataList.first().type.id)
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

    private inline fun <reified T> unreflectGetter(name: String): MethodHandle {
        return UnsafeAccess.lookup.unreflectGetter(T::class.java.getDeclaredField(name).apply { isAccessible = true })
    }

    private inline fun <reified T> unreflectSetter(name: String): MethodHandle {
        return UnsafeAccess.lookup.unreflectSetter(T::class.java.getDeclaredField(name).apply { isAccessible = true })
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
package cn.fd.ratziel.module.item.nms

import cn.fd.ratziel.function.uncheck
import cn.fd.ratziel.module.item.nbt.*
import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import net.minecraft.core.IRegistryCustom
import net.minecraft.core.component.DataComponentMap
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.resources.RegistryOps
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_20_R4.CraftServer
import taboolib.library.reflex.ReflexClass
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.nmsClass
import taboolib.module.nms.nmsProxy
import java.nio.ByteBuffer
import java.util.*
import java.util.stream.Stream
import kotlin.streams.asSequence


/**
 * NMS12005
 *
 * 用于 1.20.5+ 的 [DataComponentPatch] 相关使用
 *
 * @author TheFloodDragon
 * @since 2024/5/5 12:39
 */
abstract class NMS12005 {

    /**
     * [NBTCompound] to [DataComponentPatch]
     */
    abstract fun parsePatch(tag: NBTCompound): Any?

    /**
     * [DataComponentPatch] to [NBTCompound]
     */
    abstract fun savePatch(dcp: Any): NBTCompound?

    /**
     * [NBTCompound] to [DataComponentMap]
     */
    abstract fun parseMap(tag: NBTCompound): Any?

    /**
     * [DataComponentMap] to [NBTCompound]
     */
    abstract fun saveMap(dcm: Any): NBTCompound?

    companion object {

        val INSTANCE by lazy {
            // Version Check
            if (MinecraftVersion.majorLegacy < 12005) throw UnsupportedOperationException("NMS12005 is only available after Minecraft 1.20.5!")
            // NmsProxy
            nmsProxy<NMS12005>()
        }

        /**
         * [net.minecraft.core.component.DataComponentPatch]
         */
        val dataComponentPatchClass by lazy {
            nmsClass("DataComponentPatch")
        }

    }

}

/**
 * 代码参考自: Taboolib/nms-tag-12005
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class NMS12005Impl : NMS12005() {

    fun <T> parse(codec: Codec<T>, tag: NBTCompound): T? {
        val result = codec.parse(createSerializationContext(InternalOps), tag)
        val opt = result.resultOrPartial { error("Failed to parse: $it") }
        return if (opt.isPresent) opt.get() else null
    }

    fun <T> save(codec: Codec<T>, obj: T): NBTCompound? {
        val result = codec.encodeStart(createSerializationContext(InternalOps), obj)
        val opt = result.resultOrPartial { error("Failed to save: $it") }
        return if (opt.isPresent) opt.get() as? NBTCompound else null
    }

    override fun parsePatch(tag: NBTCompound): Any? = parse(DataComponentPatch.CODEC, tag)

    override fun savePatch(dcp: Any): NBTCompound? = save(DataComponentPatch.CODEC, dcp as DataComponentPatch)

    override fun parseMap(tag: NBTCompound): Any? = parse(DataComponentMap.CODEC, tag)

    override fun saveMap(dcm: Any): NBTCompound? = save(DataComponentMap.CODEC, dcm as DataComponentMap)

    fun <V> createSerializationContext(dynamicOps: DynamicOps<V>): RegistryOps<V> = uncheck(method.invoke(access, dynamicOps)!!)

    private val access: IRegistryCustom.Dimension by lazy { (Bukkit.getServer() as CraftServer).server.registryAccess() }

    private val method by lazy {
        val ref = ReflexClass.of(net.minecraft.core.HolderLookup.a::class.java, false)
        ref.structure.getMethodByTypeSilently("a", DynamicOps::class.java)
            ?: ref.structure.getMethodByType("createSerializationContext", DynamicOps::class.java)
    }

    private object InternalOps : DynamicOps<NBTData> {

        override fun empty() = NBTEnd

        override fun createNumeric(number: Number) = NBTAdapter.adapt(number) // TODO

        override fun createString(str: String) = NBTString(str)

        override fun remove(tag: NBTData, str: String) = if (tag is NBTCompound) tag.cloneShallow().apply { remove(str) } else tag

        override fun createList(stream: Stream<NBTData>) = NBTList().apply { addAll(stream.asSequence()) }

        override fun getStream(tag: NBTData): DataResult<Stream<NBTData>> =
            if (tag is NBTList) DataResult.success(tag.stream()) else DataResult.error { "Not a list: $tag" }

        override fun createMap(stream: Stream<Pair<NBTData, NBTData>>) =
            NBTCompound().apply { stream.forEach { put((it.first as NBTString).content, it.second) } }

        override fun getMapValues(tag: NBTData): DataResult<Stream<Pair<NBTData, NBTData>>> =
            if (tag is NBTCompound)
                DataResult.success(tag.entries.stream().map { Pair.of(this.createString(it.key), it.value) })
            else DataResult.error { "Not a map: $tag" }

        override fun mergeToMap(data1: NBTData, data2: NBTData, data3: NBTData): DataResult<NBTData> {
            if (data1 !is NBTCompound && data1 !is NBTEnd) {
                return DataResult.error({ "mergeToMap called with not a map: $data1" }, data1)
            } else if (data2 !is NBTString) {
                return DataResult.error({ "key is not a string: $data2" }, data1)
            } else {
                val newMap = (data1 as? NBTCompound)?.cloneShallow() ?: NBTCompound()
                newMap.put(data2.content, data3)
                return DataResult.success(newMap)
            }
        }

        override fun mergeToList(data1: NBTData, data2: NBTData): DataResult<NBTData> =
            if (data1 is NBTList && data2 is NBTList)
                DataResult.success(NBTList(data1.plus(data2.content))) // TODO Improve
            else DataResult.error({ "mergeToList called with not a list: $data1" }, data1)

        override fun getStringValue(data: NBTData): DataResult<String> =
            if (data is NBTString) DataResult.success(data.content) else DataResult.error { "Not a string: $data" }

        override fun getNumberValue(data: NBTData): DataResult<Number> {
            val number = data.content as? Number
            return if (number != null) DataResult.success(number) else DataResult.error { "Not a number: $data" }
        }

        override fun <U> convertTo(ops: DynamicOps<U>, data: NBTData): U =
            when (data.type.id) {
                0 -> ops.empty()
                1 -> ops.createByte((data as NBTByte).content)
                2 -> ops.createShort((data as NBTShort).content)
                3 -> ops.createInt((data as NBTInt).content)
                4 -> ops.createLong((data as NBTLong).content)
                5 -> ops.createFloat((data as NBTFloat).content)
                6 -> ops.createDouble((data as NBTDouble).content)
                7 -> ops.createByteList(ByteBuffer.wrap((data as NBTByteArray).content))
                8 -> ops.createString(data.toString())
                9 -> convertList(ops, data)
                10 -> convertMap(ops, data)
                11 -> ops.createIntList(Arrays.stream((data as NBTIntArray).content))
                12 -> ops.createLongList(Arrays.stream((data as NBTLongArray).content))
                else -> throw IllegalStateException("Unknown tag type: $data")
            }

    }

}

object NBTEnd : NBTData(Unit, NBTType.END) {
    override val content = Unit
}
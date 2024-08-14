package cn.fd.ratziel.module.item.nms

import cn.fd.ratziel.function.uncheck
import cn.fd.ratziel.module.nbt.*
import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.MapLike;
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
import java.util.stream.IntStream
import java.util.stream.LongStream
import java.util.stream.Stream


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

object NBTEnd : NBTData(NBTType.END) {
    override val content = Unit
    override fun clone() = this
}

/**
 * 代码参考自: Taboolib/nms-tag-12005
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class NMS12005Impl : NMS12005() {

    fun <T> parse(codec: Codec<T>, tag: NBTCompound): T? {
        val result = codec.parse(createSerializationContext(DataDynamicOps), tag)
        val opt = result.resultOrPartial { error("Failed to parse: $it") }
        return if (opt.isPresent) opt.get() else null
    }

    fun <T> save(codec: Codec<T>, obj: T): NBTCompound? {
        val result = codec.encodeStart(createSerializationContext(DataDynamicOps), obj)
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

    /**
     * 摘抄修改自: [net.minecraft.nbt.DynamicOpsNBT]
     */
    object DataDynamicOps : DynamicOps<NBTData> {

        override fun empty() = NBTEnd

        override fun <U> convertTo(ops: DynamicOps<U>, data: NBTData): U = when (data.type.id) {
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

        override fun remove(tag: NBTData, str: String) = if (tag is NBTCompound) tag.cloneShallow().apply { remove(str) } else tag

        override fun createNumeric(number: Number) = NBTAdapter.adapt(number)

        override fun createString(str: String) = NBTString(str)

        override fun createList(stream: Stream<NBTData>) = InitialListCollector.acceptAll(stream.iterator()).result()

        override fun createByteList(byteBuffer: ByteBuffer) = NBTByteArray(byteBuffer.array().copyOf())

        override fun getByteBuffer(data: NBTData): DataResult<ByteBuffer> =
            if (data is NBTByteArray) DataResult.success(ByteBuffer.wrap(data.content)) else super.getByteBuffer(data)

        override fun createIntList(intStream: IntStream) = NBTIntArray(intStream.toArray())

        override fun getIntStream(data: NBTData): DataResult<IntStream> =
            if (data is NBTIntArray) DataResult.success(Arrays.stream(data.content)) else super.getIntStream(data)

        override fun createLongList(longStream: LongStream) = NBTLongArray(longStream.toArray())

        override fun getLongStream(data: NBTData): DataResult<LongStream> =
            if (data is NBTLongArray) DataResult.success(Arrays.stream(data.content)) else super.getLongStream(data)

        override fun createMap(stream: Stream<Pair<NBTData, NBTData>>) =
            NBTCompound().apply { stream.forEach { put((it.first as NBTString).content, it.second) } }

        override fun getStringValue(data: NBTData): DataResult<String> =
            if (data is NBTString) DataResult.success(data.content) else DataResult.error { "Not a string: $data" }

        override fun getNumberValue(data: NBTData): DataResult<Number> {
            val number = data.content as? Number
            return if (number != null) DataResult.success(number) else DataResult.error { "Not a number: $data" }
        }

        override fun getStream(data: NBTData): DataResult<Stream<NBTData>> = when (data) {
            is NBTList -> DataResult.success(
                if (data.elementType == NBTType.COMPOUND) {
                    data.stream().map { tryUnwrap(it as NBTCompound) }
                } else data.stream())
            is NBTByteArray -> DataResult.success(data.content.toList().stream().map { NBTByte(it) })
            is NBTIntArray -> DataResult.success(data.content.toList().stream().map { NBTInt(it) })
            is NBTLongArray -> DataResult.success(data.content.toList().stream().map { NBTLong(it) })
            else -> DataResult.error { "Not a list: $data" }
        }

        override fun getMapValues(tag: NBTData): DataResult<Stream<Pair<NBTData, NBTData>>> =
            if (tag is NBTCompound)
                DataResult.success(tag.entries.stream().map { Pair.of(this.createString(it.key), it.value) })
            else DataResult.error { "Not a map: $tag" }

        override fun getMap(tag: NBTData): DataResult<MapLike<NBTData>> = if (tag is NBTCompound) {
            DataResult.success(object : MapLike<NBTData> {
                override fun get(data: NBTData): NBTData? = get((data as NBTString).content)
                override fun get(str: String): NBTData? = tag[str]
                override fun entries(): Stream<Pair<NBTData, NBTData>> = tag.entries.stream().map { Pair.of(createString(it.key), it.value) }
                override fun toString() =  "MapLike[" + tag + "]"
            })
        } else DataResult.error { "Not a map: $tag" }

        override fun mergeToMap(data1: NBTData, data2: NBTData, data3: NBTData): DataResult<NBTData> {
            if (data1 !is NBTCompound && data1 !is NBTEnd) {
                return DataResult.error({ "mergeToMap called with not a map: $data1" }, data1)
            } else if (data2 !is NBTString) {
                return DataResult.error({ "key is not a string: $data2" }, data1)
            } else {
                val newMap = (data1 as? NBTCompound)?.cloneShallow() ?: NBTCompound()
                newMap[data2.content] = data3
                return DataResult.success(newMap)
            }
        }

        override fun mergeToList(data1: NBTData, data2: NBTData): DataResult<NBTData> {
            val collector = createCollector(data1)
            return if (collector != null) DataResult.success(collector.accept(data2).result())
            else DataResult.error({ "mergeToList called with not a list: $data1" }, data1)
        }

        fun isWrapper(tag: NBTCompound) = tag.content.size == 1 && tag.contains("")
        fun wrapElement(data: NBTData) = NBTCompound().apply { put("", data) }
        fun wrapIfNeeded(data: NBTData) = if (data is NBTCompound && !isWrapper(data)) data else wrapElement(data)
        fun tryUnwrap(tag: NBTCompound) = tag[""] ?: tag

        fun createCollector(data: NBTData): ListCollector? = when (data) {
            is NBTEnd -> InitialListCollector
            is NBTList -> when (data.type) {
                NBTType.END -> InitialListCollector
                NBTType.COMPOUND -> HeterogenousListCollector(data)
                else -> HomogenousListCollector(data)
            }

            is NBTByteArray -> ByteListCollector(data.content.toMutableList())
            is NBTIntArray -> IntListCollector(data.content.toMutableList())
            is NBTLongArray -> LongListCollector(data.content.toMutableList())
            else -> null
        }

        class HeterogenousListCollector(val result: NBTList = NBTList()) : ListCollector {
            override fun accept(data: NBTData) = this.apply { result.add(wrapIfNeeded(data)) }
            override fun result() = this.result
        }

        class HomogenousListCollector(val result: NBTList = NBTList()) : ListCollector {
            override fun result() = this.result
            override fun accept(data: NBTData) =
                if (data.type == result.elementType) this.apply { result.add(data) } else HeterogenousListCollector().acceptAll(result).accept(data)
        }

        object InitialListCollector : ListCollector {
            override fun result() = NBTList()
            override fun accept(data: NBTData) = when (data) {
                is NBTCompound -> HeterogenousListCollector().accept(data)
                is NBTByte -> ByteListCollector(mutableListOf(data.content))
                is NBTInt -> IntListCollector(mutableListOf(data.content))
                is NBTLong -> LongListCollector(mutableListOf(data.content))
                else -> HomogenousListCollector(NBTList().apply { add(data) })
            }
        }

        class ByteListCollector(val values: MutableList<Byte> = mutableListOf()) : ListCollector {
            override fun result() = NBTByteArray(values.toByteArray())
            override fun accept(data: NBTData) =
                if (data is NBTByte) this.apply { values.add(data.content) }
                else HeterogenousListCollector(NBTList().apply { values.forEach { add(wrapElement(NBTByte(it))) } }).accept(data)
        }

        class IntListCollector(val values: MutableList<Int> = mutableListOf()) : ListCollector {
            override fun result() = NBTIntArray(values.toIntArray())
            override fun accept(data: NBTData) =
                if (data is NBTInt) this.apply { values.add(data.content) }
                else HeterogenousListCollector(NBTList().apply { values.forEach { add(wrapElement(NBTInt(it))) } }).accept(data)
        }

        class LongListCollector(val values: MutableList<Long> = mutableListOf()) : ListCollector {
            override fun result() = NBTLongArray(values.toLongArray())
            override fun accept(data: NBTData) =
                if (data is NBTLong) this.apply { values.add(data.content) }
                else HeterogenousListCollector(NBTList().apply { values.forEach { add(wrapElement(NBTLong(it))) } }).accept(data)
        }

    }

}

interface ListCollector {
    fun accept(data: NBTData): ListCollector
    fun result(): NBTData
    fun acceptAll(iterable: Iterable<NBTData>) = acceptAll(iterable.iterator())
    fun acceptAll(iterator: Iterator<NBTData>): ListCollector {
        var collector = this
        iterator.forEach { collector = collector.accept(it) }
        return collector
    }
}
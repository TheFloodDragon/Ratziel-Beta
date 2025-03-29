package cn.fd.ratziel.module.item.internal.nms

import cn.altawk.nbt.tag.*
import cn.fd.ratziel.module.nbt.NbtAdapter
import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.MapLike
import net.minecraft.core.component.DataComponentMap
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.resources.RegistryOps
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_20_R4.CraftServer
import taboolib.library.reflex.ReflexClass
import taboolib.module.nms.MinecraftVersion
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
     * [NbtCompound] to [DataComponentPatch]
     */
    abstract fun parsePatch(tag: NbtCompound): Any?

    /**
     * [DataComponentPatch] to [NbtCompound]
     */
    abstract fun savePatch(dcp: Any): NbtCompound?

    /**
     * [NbtCompound] to [DataComponentMap]
     */
    abstract fun parseMap(tag: NbtCompound): Any?

    /**
     * [DataComponentMap] to [NbtCompound]
     */
    abstract fun saveMap(dcm: Any): NbtCompound?

    companion object {

        val INSTANCE by lazy {
            if (MinecraftVersion.versionId >= 12005) nmsProxy<NMS12005>() else throw UnsupportedOperationException("NMS12005 is only available after Minecraft 1.20.5!")
        }

    }

}

@Suppress("unused", "MemberVisibilityCanBePrivate")
class NMS12005Impl : NMS12005() {

    fun <T> parse0(codec: Codec<T>, obj: NbtTag): T? {
        val result = codec.parse(access.createSerializationContext(DataDynamicOps), obj)
        val opt = result.resultOrPartial { error("Failed to parse: $it") }
        return if (opt.isPresent) opt.get() else null
    }

    fun <T> save0(codec: Codec<T>, obj: T): NbtTag? {
        val result = codec.encodeStart(access.createSerializationContext(DataDynamicOps), obj)
        val opt = result.resultOrPartial { error("Failed to save: $it") }
        return if (opt.isPresent) opt.get() else null
    }

    fun <T> parseFromTag(codec: Codec<T>, tag: NbtCompound): T? {
        return parse0(codec, tag)
    }

    fun <T> saveToTag(codec: Codec<T>, obj: T): NbtCompound? {
        return save0(codec, obj) as? NbtCompound
    }

    override fun parsePatch(tag: NbtCompound): Any? = parseFromTag(DataComponentPatch.CODEC, tag)

    override fun savePatch(dcp: Any): NbtCompound? = saveToTag(DataComponentPatch.CODEC, dcp as DataComponentPatch)

    override fun parseMap(tag: NbtCompound): Any? = parseFromTag(DataComponentMap.CODEC, tag)

    override fun saveMap(dcm: Any): NbtCompound? = saveToTag(DataComponentMap.CODEC, dcm as DataComponentMap)

    private val access: net.minecraft.core.HolderLookup.a by lazy {
        (Bukkit.getServer() as CraftServer).server.registryAccess()
    }

    private val method by lazy {
        val ref = ReflexClass.of(net.minecraft.core.HolderLookup.a::class.java, false)
        ref.structure.getMethodByTypeSilently("a", DynamicOps::class.java)
            ?: ref.structure.getMethodByType("createSerializationContext", DynamicOps::class.java)
    }

    @Suppress("UNCHECKED_CAST")
    fun <V> createSerializationContext(dynamicOps: DynamicOps<V>): RegistryOps<V> = method.invoke(access, dynamicOps)!! as RegistryOps<V>

    /**
     * 摘抄修改自: [net.minecraft.nbt.DynamicOpsNBT]
     */
    object DataDynamicOps : DynamicOps<NbtTag> {

        // 伪 NbtEnd
        val NbtEnd = NbtCompound()

        override fun empty() = NbtEnd

        override fun <U> convertTo(ops: DynamicOps<U>, data: NbtTag): U = when (data.type.id.toInt()) {
            0 -> ops.empty()
            1 -> ops.createByte((data as NbtByte).content)
            2 -> ops.createShort((data as NbtShort).content)
            3 -> ops.createInt((data as NbtInt).content)
            4 -> ops.createLong((data as NbtLong).content)
            5 -> ops.createFloat((data as NbtFloat).content)
            6 -> ops.createDouble((data as NbtDouble).content)
            7 -> ops.createByteList(ByteBuffer.wrap((data as NbtByteArray).content))
            8 -> ops.createString(data.toString())
            9 -> convertList(ops, data)
            10 -> convertMap(ops, data)
            11 -> ops.createIntList(Arrays.stream((data as NbtIntArray).content))
            12 -> ops.createLongList(Arrays.stream((data as NbtLongArray).content))
            else -> throw IllegalStateException("Unknown tag type: $data")
        }

        override fun remove(tag: NbtTag, str: String) = if (tag is NbtCompound) tag.clone().apply { remove(str) } else tag

        override fun createNumeric(number: Number) = NbtAdapter.box(number)

        override fun createString(str: String) = NbtString(str)

        override fun createList(stream: Stream<NbtTag>) = InitialListCollector.acceptAll(stream.iterator()).result()

        override fun createByteList(byteBuffer: ByteBuffer) = NbtByteArray(byteBuffer.array().copyOf())

        override fun getByteBuffer(data: NbtTag): DataResult<ByteBuffer> =
            if (data is NbtByteArray) DataResult.success(ByteBuffer.wrap(data.content)) else super.getByteBuffer(data)

        override fun createIntList(intStream: IntStream) = NbtIntArray(intStream.toArray())

        override fun getIntStream(data: NbtTag): DataResult<IntStream> =
            if (data is NbtIntArray) DataResult.success(Arrays.stream(data.content)) else super.getIntStream(data)

        override fun createLongList(longStream: LongStream) = NbtLongArray(longStream.toArray())

        override fun getLongStream(data: NbtTag): DataResult<LongStream> =
            if (data is NbtLongArray) DataResult.success(Arrays.stream(data.content)) else super.getLongStream(data)

        override fun createMap(stream: Stream<Pair<NbtTag, NbtTag>>) =
            NbtCompound().apply { stream.forEach { put((it.first as NbtString).content, it.second) } }

        override fun getStringValue(data: NbtTag): DataResult<String> =
            if (data is NbtString) DataResult.success(data.content) else DataResult.error { "Not a string: $data" }

        override fun getNumberValue(data: NbtTag): DataResult<Number> {
            val number = data.content as? Number
            return if (number != null) DataResult.success(number) else DataResult.error { "Not a number: $data" }
        }

        override fun getStream(data: NbtTag): DataResult<Stream<NbtTag>> = when (data) {
            is NbtList -> DataResult.success(
                if (data.elementType == NbtType.COMPOUND) {
                    data.stream().map { tryUnwrap(it as NbtCompound) }
                } else data.stream())

            is NbtByteArray -> DataResult.success(data.content.toList().stream().map { NbtByte(it) })
            is NbtIntArray -> DataResult.success(data.content.toList().stream().map { NbtInt(it) })
            is NbtLongArray -> DataResult.success(data.content.toList().stream().map { NbtLong(it) })
            else -> DataResult.error { "Not a list: $data" }
        }

        override fun getMapValues(tag: NbtTag): DataResult<Stream<Pair<NbtTag, NbtTag>>> =
            if (tag is NbtCompound)
                DataResult.success(tag.entries.stream().map { Pair.of(this.createString(it.key), it.value) })
            else DataResult.error { "Not a map: $tag" }

        override fun getMap(tag: NbtTag): DataResult<MapLike<NbtTag>> = if (tag is NbtCompound) {
            DataResult.success(object : MapLike<NbtTag> {
                override fun get(data: NbtTag): NbtTag? = get((data as NbtString).content)
                override fun get(str: String): NbtTag? = tag[str]
                override fun entries(): Stream<Pair<NbtTag, NbtTag>> = tag.entries.stream().map { Pair.of(createString(it.key), it.value) }
                override fun toString() = "MapLike[$tag]"
            })
        } else DataResult.error { "Not a map: $tag" }

        override fun mergeToMap(data1: NbtTag, data2: NbtTag, data3: NbtTag): DataResult<NbtTag> {
            if (data1 !is NbtCompound && data1 !== NbtEnd) {
                return DataResult.error({ "mergeToMap called with not a map: $data1" }, data1)
            } else if (data2 !is NbtString) {
                return DataResult.error({ "key is not a string: $data2" }, data1)
            } else {
                val newMap = data1.clone()
                newMap[data2.content] = data3
                return DataResult.success(newMap)
            }
        }

        override fun mergeToList(data1: NbtTag, data2: NbtTag): DataResult<NbtTag> {
            val collector = createCollector(data1)
            return if (collector != null) DataResult.success(collector.accept(data2).result())
            else DataResult.error({ "mergeToList called with not a list: $data1" }, data1)
        }

        fun isWrapper(tag: NbtCompound) = tag.content.size == 1 && tag.contains("")
        fun wrapElement(data: NbtTag) = NbtCompound().apply { put("", data) }
        fun wrapIfNeeded(data: NbtTag) = if (data is NbtCompound && !isWrapper(data)) data else wrapElement(data)
        fun tryUnwrap(tag: NbtCompound) = tag[""] ?: tag

        fun createCollector(data: NbtTag): ListCollector? = when (data) {
            is NbtList -> when (data.type) {
                NbtType.END -> InitialListCollector
                NbtType.COMPOUND -> HeterogenousListCollector(data)
                else -> HomogenousListCollector(data)
            }

            is NbtByteArray -> ByteListCollector(data.content.toMutableList())
            is NbtIntArray -> IntListCollector(data.content.toMutableList())
            is NbtLongArray -> LongListCollector(data.content.toMutableList())
            else -> if (data === NbtEnd) InitialListCollector else null
        }

        class HeterogenousListCollector(val result: NbtList = NbtList()) : ListCollector {
            override fun accept(data: NbtTag) = this.apply { result.add(wrapIfNeeded(data)) }
            override fun result() = this.result
        }

        class HomogenousListCollector(val result: NbtList = NbtList()) : ListCollector {
            override fun result() = this.result
            override fun accept(data: NbtTag) =
                if (data.type == result.elementType) this.apply { result.add(data) } else HeterogenousListCollector().acceptAll(result).accept(data)
        }

        object InitialListCollector : ListCollector {
            override fun result() = NbtList()
            override fun accept(data: NbtTag) = when (data) {
                is NbtCompound -> HeterogenousListCollector().accept(data)
                is NbtByte -> ByteListCollector(mutableListOf(data.content))
                is NbtInt -> IntListCollector(mutableListOf(data.content))
                is NbtLong -> LongListCollector(mutableListOf(data.content))
                else -> HomogenousListCollector(NbtList().apply { add(data) })
            }
        }

        class ByteListCollector(val values: MutableList<Byte> = mutableListOf()) : ListCollector {
            override fun result() = NbtByteArray(values.toByteArray())
            override fun accept(data: NbtTag) =
                if (data is NbtByte) this.apply { values.add(data.content) }
                else HeterogenousListCollector(NbtList().apply { values.forEach { add(wrapElement(NbtByte(it))) } }).accept(data)
        }

        class IntListCollector(val values: MutableList<Int> = mutableListOf()) : ListCollector {
            override fun result() = NbtIntArray(values.toIntArray())
            override fun accept(data: NbtTag) =
                if (data is NbtInt) this.apply { values.add(data.content) }
                else HeterogenousListCollector(NbtList().apply { values.forEach { add(wrapElement(NbtInt(it))) } }).accept(data)
        }

        class LongListCollector(val values: MutableList<Long> = mutableListOf()) : ListCollector {
            override fun result() = NbtLongArray(values.toLongArray())
            override fun accept(data: NbtTag) =
                if (data is NbtLong) this.apply { values.add(data.content) }
                else HeterogenousListCollector(NbtList().apply { values.forEach { add(wrapElement(NbtLong(it))) } }).accept(data)
        }

    }

}

interface ListCollector {
    fun accept(data: NbtTag): ListCollector
    fun result(): NbtTag
    fun acceptAll(iterable: Iterable<NbtTag>) = acceptAll(iterable.iterator())
    fun acceptAll(iterator: Iterator<NbtTag>): ListCollector {
        var collector = this
        iterator.forEach { collector = collector.accept(it) }
        return collector
    }
}

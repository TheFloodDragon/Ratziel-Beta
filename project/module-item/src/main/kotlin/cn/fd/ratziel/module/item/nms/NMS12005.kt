package cn.fd.ratziel.module.item.nms

import cn.fd.ratziel.function.uncheck
import cn.fd.ratziel.module.nbt.*
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

        override fun createList(stream: Stream<NBTData>) = NBTList().apply { addAll(stream.asSequence()) }

        override fun createMap(stream: Stream<Pair<NBTData, NBTData>>) =
            NBTCompound().apply { stream.forEach { put((it.first as NBTString).content, it.second) } }

        override fun getStringValue(data: NBTData): DataResult<String> =
            if (data is NBTString) DataResult.success(data.content) else DataResult.error { "Not a string: $data" }

        override fun getNumberValue(data: NBTData): DataResult<Number> {
            val number = data.content as? Number
            return if (number != null) DataResult.success(number) else DataResult.error { "Not a number: $data" }
        }

        override fun getStream(tag: NBTData): DataResult<Stream<NBTData>> =
            if (tag is NBTList) {
                DataResult.success(if (tag.elementType == NBTType.COMPOUND) {
                    tag.stream().map { tryUnwrap(it as NBTCompound) }
                } else tag.stream())
            } else DataResult.error { "Not a list: $tag" }

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
        fun tryUnwrap(tag: NBTCompound): NBTData {
            if (tag.content.size == 1) {
                val unwrap = tag[""]
                if (unwrap != null) return unwrap
            }
            return tag
        }

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

        interface ListCollector {
            fun accept(data: NBTData): ListCollector
            fun result(): NBTData
            fun acceptAll(iterable: Iterable<NBTData>): ListCollector {
                var collector = this
                iterable.forEach { collector = collector.accept(it) }
                return collector
            }
        }

        class HeterogenousListCollector(val result: NBTList = NBTList()) : ListCollector {
            constructor(list: List<Int>) : this(NBTList(list.map { wrapElement(NBTInt(it)) }))
            constructor(list: List<Byte>) : this(NBTList(list.map { wrapElement(NBTByte(it)) }))
            constructor(list: List<Long>) : this(NBTList(list.map { wrapElement(NBTLong(it)) }))

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
                else HeterogenousListCollector(values).accept(data)
        }

        class IntListCollector(val values: MutableList<Int> = mutableListOf()) : ListCollector {
            override fun result() = NBTIntArray(values.toIntArray())
            override fun accept(data: NBTData) =
                if (data is NBTInt) this.apply { values.add(data.content) }
                else HeterogenousListCollector(values).accept(data)
        }

        class LongListCollector(val values: MutableList<Long> = mutableListOf()) : ListCollector {
            override fun result() = NBTLongArray(values.toLongArray())
            override fun accept(data: NBTData) =
                if (data is NBTLong) this.apply { values.add(data.content) }
                else HeterogenousListCollector(values).accept(data)
        }

    }

}
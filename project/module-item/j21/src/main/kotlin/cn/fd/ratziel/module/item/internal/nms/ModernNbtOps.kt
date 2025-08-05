package cn.fd.ratziel.module.item.internal.nms

import cn.altawk.nbt.tag.*
import com.google.common.collect.Lists
import com.mojang.datafixers.util.Pair
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.MapLike
import com.mojang.serialization.RecordBuilder
import it.unimi.dsi.fastutil.bytes.ByteArrayList
import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.longs.LongArrayList
import java.nio.ByteBuffer
import java.util.*
import java.util.function.BiConsumer
import java.util.function.Consumer
import java.util.stream.Collectors
import java.util.stream.IntStream
import java.util.stream.LongStream
import java.util.stream.Stream


/**
 * ModernNbtOps
 *
 * @author TheFloodDragon
 * @since 2025/8/5 10:25
 */
object ModernNbtOps : DynamicOps<NbtTag> {

    // TODO remove this after updated altawk nbt
    fun NbtCompound.cloneShallow() = NbtCompound { putAll(this@cloneShallow.content) }

    override fun empty(): NbtEnd = NbtEnd.INSTANCE

    override fun <U> convertTo(ops: DynamicOps<U>, tag: NbtTag): U = when (tag) {
        is NbtEnd -> ops.empty()
        is NbtByte -> ops.createByte(tag.content)
        is NbtShort -> ops.createShort(tag.content)
        is NbtInt -> ops.createInt(tag.content)
        is NbtLong -> ops.createLong(tag.content)
        is NbtFloat -> ops.createFloat(tag.content)
        is NbtDouble -> ops.createDouble(tag.content)
        is NbtByteArray -> ops.createByteList(ByteBuffer.wrap(tag.content))
        is NbtString -> ops.createString(tag.content)
        is NbtList -> this.convertList(ops, tag)
        is NbtCompound -> this.convertMap(ops, tag)
        is NbtIntArray -> ops.createIntList(Arrays.stream(tag.content))
        is NbtLongArray -> ops.createLongList(Arrays.stream(tag.content))
    }

    override fun getNumberValue(tag: NbtTag): DataResult<Number> {
        return (tag.content as? Number)?.let { DataResult.success(it) } ?: DataResult.error { "Not a number: $tag" }
    }

    override fun createNumeric(number: Number) = NbtDouble(number.toDouble())
    override fun createByte(b: Byte) = NbtByte(b)
    override fun createShort(s: Short) = NbtShort(s)
    override fun createInt(i: Int) = NbtInt(i)
    override fun createLong(l: Long) = NbtLong(l)
    override fun createFloat(f: Float) = NbtFloat(f)
    override fun createDouble(d: Double) = NbtDouble(d)
    override fun createBoolean(b: Boolean) = NbtByte(b)
    override fun createString(string: String) = NbtString(string)
    override fun createList(tag: Stream<NbtTag>) = NbtList(tag.collect(Collectors.toCollection(Lists::newArrayList)))
    override fun createIntList(input: IntStream) = NbtIntArray(input.toArray())
    override fun createLongList(input: LongStream) = NbtLongArray(input.toArray())
    override fun createByteList(tag: ByteBuffer): NbtByteArray {
        val byteBuffer = tag.duplicate().clear()
        val bytes = ByteArray(tag.capacity())
        byteBuffer.get(0, bytes, 0, bytes.size)
        return NbtByteArray(bytes)
    }

    override fun getStringValue(tag: NbtTag): DataResult<String> {
        return if (tag is NbtString) {
            DataResult.success(tag.content)
        } else {
            DataResult.error { "Not a string" }
        }
    }

    override fun mergeToList(tag: NbtTag, tag2: NbtTag): DataResult<NbtTag> {
        return this.createCollector(tag)
            ?.let { merger -> DataResult.success(merger.accept(tag2).result()) }
            ?: DataResult.error({ "mergeToList called with not a list: $tag" }, tag)
    }

    override fun mergeToList(tag: NbtTag, list: MutableList<NbtTag>): DataResult<NbtTag>? {
        return this.createCollector(tag)
            ?.let { merger -> DataResult.success(merger.acceptAll(list).result()) }
            ?: DataResult.error({ "mergeToList called with not a list: $tag" }, tag)
    }

    override fun mergeToMap(map: NbtTag, key: NbtTag, value: NbtTag): DataResult<NbtTag> {
        if (map !is NbtCompound && map !is NbtEnd) {
            return DataResult.error({ "mergeToMap called with not a map: $map" }, map)
        } else if (key is NbtString) {
            val result = if (map is NbtCompound) map.cloneShallow() else NbtCompound()
            result.put(key.content, value)
            return DataResult.success(result)
        } else {
            return DataResult.error({ "key is not a string: $key" }, map)
        }
    }

    override fun mergeToMap(map: NbtTag, otherMap: MapLike<NbtTag>): DataResult<NbtTag>? {
        if (map !is NbtCompound && map !is NbtEnd) {
            return DataResult.error({ "mergeToMap called with not a map: $map" }, map)
        } else {
            val result = if (map is NbtCompound) map.cloneShallow() else NbtCompound()
            val invalidKeys: MutableList<NbtTag> = ArrayList<NbtTag>()
            otherMap.entries().forEach { pair ->
                val keyTag: NbtTag = pair.getFirst()
                if (keyTag is NbtString) {
                    result.put(keyTag.content, pair.getSecond())
                } else {
                    invalidKeys.add(keyTag)
                }
            }
            return if (invalidKeys.isEmpty()) DataResult.success(result) else DataResult.error({ "Invalid keys: $invalidKeys" }, result)
        }
    }

    override fun mergeToMap(inputTag: NbtTag, entriesToMerge: MutableMap<NbtTag, NbtTag>): DataResult<NbtTag>? {
        if (inputTag !is NbtCompound && inputTag !is NbtEnd) {
            return DataResult.error({ "mergeToMap called with not a map: $inputTag" }, inputTag)
        }
        val result = if (inputTag is NbtCompound) inputTag.cloneShallow() else NbtCompound()
        val invalidKeys: MutableList<NbtTag> = ArrayList<NbtTag>()
        for (entry in entriesToMerge.entries) {
            val keyTag: NbtTag = entry.key
            if (keyTag is NbtString) {
                result.put(keyTag.content, entry.value)
            } else {
                invalidKeys.add(keyTag)
            }
        }
        return if (invalidKeys.isEmpty()) DataResult.success(result) else DataResult.error({ "Found non-string keys: $invalidKeys" }, result)
    }


    override fun getMapValues(map: NbtTag): DataResult<Stream<Pair<NbtTag, NbtTag>?>?> {
        return if (map is NbtCompound)
            DataResult.success(map.entries.stream().map { (key, value) -> Pair.of(this.createString(key), value) })
        else
            DataResult.error { "Not a map: $map" }
    }

    override fun getMapEntries(map: NbtTag): DataResult<Consumer<BiConsumer<NbtTag, NbtTag>>> {
        return if (map is NbtCompound) DataResult.success(Consumer<BiConsumer<NbtTag, NbtTag>> {
            for ((key, value) in map.entries) {
                it.accept(this.createString(key), value)
            }
        }) else DataResult.error { "Not a map: $map" }
    }

    override fun getMap(map: NbtTag): DataResult<MapLike<NbtTag>?> {
        return if (map is NbtCompound) DataResult.success<MapLike<NbtTag>?>(object : MapLike<NbtTag> {
            override fun get(tag: NbtTag): NbtTag? {
                if (tag is NbtString) {
                    return map[tag.content]
                } else throw UnsupportedOperationException("Cannot get map entry with non-string key: $tag")
            }

            override fun get(string: String) = map[string]

            override fun entries(): Stream<Pair<NbtTag, NbtTag>> {
                return map.entries.stream().map { (key, value) -> Pair.of(this@ModernNbtOps.createString(key), value) }
            }

            override fun toString(): String {
                return "MapLike[$map]"
            }
        }) else DataResult.error { "Not a map: $map" }
    }


    override fun createMap(stream: Stream<Pair<NbtTag, NbtTag>>) = NbtCompound {
        stream.forEach {
            val key = it.first
            if (key is NbtString) {
                put(key.content, it.second)
            } else {
                throw UnsupportedOperationException("Cannot create map with non-string key: $key")
            }
        }
    }

    override fun getStream(tag: NbtTag): DataResult<Stream<NbtTag>> = when (tag) {
        is NbtList -> DataResult.success(tag.content.stream())
        is NbtByteArray -> DataResult.success((Arrays.stream(tag.content.toTypedArray())).map<NbtTag> { NbtByte(it) })
        is NbtIntArray -> DataResult.success((Arrays.stream(tag.content.toTypedArray())).map<NbtTag> { NbtInt(it) })
        is NbtLongArray -> DataResult.success((Arrays.stream(tag.content.toTypedArray())).map<NbtTag> { NbtLong(it) })
        else -> DataResult.error { "Not a list" }
    }

    override fun getList(tag: NbtTag): DataResult<Consumer<Consumer<NbtTag>>> = when (tag) {
        is NbtList -> DataResult.success(Consumer { for (value in tag) it.accept(value) })
        is NbtByteArray -> DataResult.success(Consumer { for (value in tag.content) it.accept(NbtByte(value)) })
        is NbtIntArray -> DataResult.success(Consumer { for (value in tag.content) it.accept(NbtInt(value)) })
        is NbtLongArray -> DataResult.success(Consumer { for (value in tag.content) it.accept(NbtLong(value)) })
        else -> DataResult.error { "Not a list" }
    }

    override fun getByteBuffer(tag: NbtTag): DataResult<ByteBuffer> =
        if (tag is NbtByteArray)
            DataResult.success(ByteBuffer.wrap(tag.content))
        else getStream(tag).flatMap { stream ->
            val list: MutableList<NbtTag> = stream.toList()
            if (list.stream().allMatch { getNumberValue(it).result().isPresent }) {
                val buffer = ByteBuffer.wrap(ByteArray(list.size))
                for (i in list.indices) {
                    buffer.put(i, getNumberValue(list[i]).result().get().toByte())
                }
                return@flatMap DataResult.success(buffer)
            }
            DataResult.error { "Some elements are not bytes: $tag" }
        }

    override fun getIntStream(tag: NbtTag): DataResult<IntStream> =
        if (tag is NbtIntArray)
            DataResult.success(Arrays.stream(tag.content))
        else getStream(tag).flatMap { stream ->
            val list: MutableList<NbtTag> = stream.toList()
            if (list.stream().allMatch { getNumberValue(it).result().isPresent }) {
                return@flatMap DataResult.success(list.stream().mapToInt { getNumberValue(it).result().get().toInt() })
            }
            DataResult.error { "Some elements are not ints: $tag" }
        }

    override fun getLongStream(tag: NbtTag): DataResult<LongStream> =
        if (tag is NbtLongArray)
            DataResult.success(Arrays.stream(tag.content))
        else getStream(tag).flatMap { stream ->
            val list: MutableList<NbtTag> = stream.toList()
            if (list.stream().allMatch { getNumberValue(it).result().isPresent }) {
                return@flatMap DataResult.success<LongStream?>(
                    list.stream().mapToLong { getNumberValue(it).result().get().toLong() })
            }
            DataResult.error { "Some elements are not longs: $tag" }
        }


    override fun remove(map: NbtTag, removeKey: String): NbtTag {
        if (map is NbtCompound) {
            val copied = map.cloneShallow()
            copied.remove(removeKey)
            return copied
        } else {
            return map
        }
    }

    override fun mapBuilder() = NbtRecordBuilder(this)

    fun createCollector(tag: NbtTag): ListCollector? = when (tag) {
        is NbtEnd -> GenericListCollector()
        is NbtList -> GenericListCollector(tag)
        is NbtByteArray -> if (tag.content.isNotEmpty()) ByteListCollector(tag.content) else GenericListCollector()
        is NbtIntArray -> if (tag.content.isNotEmpty()) IntListCollector(tag.content) else GenericListCollector()
        is NbtLongArray -> if (tag.content.isNotEmpty()) LongListCollector(tag.content) else GenericListCollector()
        else -> null
    }

    override fun toString(): String {
        return "Modern-NbtOps-For-Altawk-Nbt"
    }

    interface ListCollector {
        fun accept(tag: NbtTag): ListCollector
        fun result(): NbtTag
        fun acceptAll(iterable: Iterable<NbtTag>) = acceptAll(iterable.iterator())
        fun acceptAll(iterator: Iterator<NbtTag>): ListCollector {
            var collector = this
            iterator.forEach { collector = collector.accept(it) }
            return collector
        }
    }

    class GenericListCollector(private val result: NbtList = NbtList()) : ListCollector {
        constructor(list: IntArrayList) : this() {
            list.forEach { this.result.add(NbtInt(it)) }
        }

        constructor(list: ByteArrayList) : this() {
            list.forEach { this.result.add(NbtByte(it)) }
        }

        constructor(list: LongArrayList) : this() {
            list.forEach { this.result.add(NbtLong(it)) }
        }

        override fun accept(tag: NbtTag) = this.apply { result.add(tag) }
        override fun result() = this.result
    }

    class ByteListCollector(values: ByteArray) : ListCollector {
        private val values = ByteArrayList()

        init {
            this.values.addElements(0, values)
        }

        override fun result() = NbtByteArray(values.toByteArray())
        override fun accept(tag: NbtTag): ListCollector {
            if (tag is NbtByte) {
                this.values.add(tag.content)
                return this
            }
            return GenericListCollector(this.values).accept(tag)
        }
    }

    class IntListCollector(values: IntArray) : ListCollector {
        private val values = IntArrayList()

        init {
            this.values.addElements(0, values)
        }

        override fun result() = NbtIntArray(values.toIntArray())
        override fun accept(tag: NbtTag): ListCollector {
            if (tag is NbtInt) {
                this.values.add(tag.content)
                return this
            }
            return GenericListCollector(this.values).accept(tag)
        }
    }

    class LongListCollector(values: LongArray) : ListCollector {
        private val values = LongArrayList()

        init {
            this.values.addElements(0, values)
        }

        override fun result() = NbtLongArray(values.toLongArray())
        override fun accept(tag: NbtTag): ListCollector {
            if (tag is NbtLong) {
                this.values.add(tag.content)
                return this
            }
            return GenericListCollector(this.values).accept(tag)
        }
    }

    class NbtRecordBuilder(ops: DynamicOps<NbtTag>) : RecordBuilder.AbstractStringBuilder<NbtTag, NbtCompound>(ops) {
        override fun initBuilder() = NbtCompound()
        override fun append(key: String, value: NbtTag, tag: NbtCompound) = tag.apply { put(key, value) }
        override fun build(compoundTag: NbtCompound, tag: NbtTag?): DataResult<NbtTag> {
            if (tag != null && tag !== NbtEnd.INSTANCE) {
                if (tag !is NbtCompound) {
                    return DataResult.error({ "mergeToMap called with not a map: $tag" }, tag)
                }
                val copied = tag.cloneShallow()
                for ((key, value) in compoundTag) {
                    copied.put(key, value)
                }
                return DataResult.success(copied)
            }
            return DataResult.success(compoundTag)
        }
    }

}
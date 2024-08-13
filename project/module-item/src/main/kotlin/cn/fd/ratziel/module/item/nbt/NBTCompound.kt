package cn.fd.ratziel.module.item.nbt

import cn.fd.ratziel.core.exception.UnsupportedTypeException
import cn.fd.ratziel.function.util.uncheck

/**
 * NBTCompound
 *
 * @author TheFloodDragon
 * @since 2024/3/15 19:28
 */
open class NBTCompound private constructor(rawData: Any) : NBTData(rawData, NBTType.COMPOUND), MutableMap<String, NBTData> {

    constructor() : this(new())

    constructor(map: Map<*, *>) : this(NBTAdapter.adaptMap(map).getRaw())

    /**
     * [java.util.Map] is the same as [MutableMap] in [kotlin.collections]
     * Because [MutableMap] will be compiled to [java.util.Map]
     * And this is final, so it's not getter
     */
    internal val sourceMap: MutableMap<String, Any> = uncheck(NMSUtil.NtCompound.sourceField.get(data)!!)

    /**
     * 内容
     */
    override val content: Map<String, NBTData> get() = buildMap { sourceMap.forEach { put(it.key, NBTAdapter.adaptNms(it.value)) } }

    /**
     * 获取数据
     * @param key 节点
     */
    override operator fun get(key: String): NBTData? = sourceMap[key]?.let { NBTAdapter.adaptNms(it) }

    /**
     * 写入数据
     * @param key 节点
     * @param value NBT数据
     */
    override fun put(key: String, value: NBTData) = sourceMap.put(key, value.getRaw())?.let { NBTAdapter.adaptNms(it) }

    /**
     * 删除数据
     * @param key 节点
     */
    override fun remove(key: String) = sourceMap.remove(key)?.let { NBTAdapter.adaptNms(it) }

    /**
     * 克隆数据
     */
    open fun clone() = this.apply { data = NMSUtil.NtCompound.methodClone.invoke(data)!! }

    /**
     * 合并目标数据
     * @param replace 是否替换原有的数据 (false时不会 替换/删除 任何一个节点)
     */
    open fun merge(target: NBTCompound, replace: Boolean = true): NBTCompound {
        target.sourceMap.forEach { (key, targetValue) ->
            // 获取自身的数据
            val ownValue = this.sourceMap[key]
            // 如果自身数据不存在, 或者允许替换, 则直接替换, 反则跳出循环
            this.sourceMap[key] = when {
                // 目标值为 Compound 类型
                NMSUtil.NtCompound.isOwnClass(targetValue::class.java) -> ownValue
                    ?.takeIf { NMSUtil.NtCompound.isOwnClass(it::class.java) } // 若自身为 Compound 类型
                    ?.let { NBTCompound(it).merge(NBTCompound(targetValue), replace) }// 同类型合并
                    ?.getRaw()
                // 目标值为基础类型, 或者 List 类型
                else -> null
            } ?: if (ownValue == null || replace) targetValue else return@forEach
        }
        return this
    }

    /**
     * 合并目标数据 (浅合并)
     * @param replace 是否替换原有的标签
     */
    open fun mergeShallow(target: NBTCompound, replace: Boolean = true): NBTCompound {
        target.sourceMap.forEach { (key, value) ->
            // 如果当前NBT数据中存在, 且不允许替换, 则直接跳出循环
            if (this.sourceMap.containsKey(key) && !replace) return@forEach
            // 反则设置值
            this.sourceMap[key] = value
        }
        return this
    }

    companion object {

        @JvmStatic
        fun new() = new(HashMap())

        @JvmStatic
        fun new(map: Map<String, Any>) = NMSUtil.NtCompound.constructor.instance(map)!!

        @JvmStatic
        fun of(raw: Any) = if (NMSUtil.NtCompound.isOwnClass(raw::class.java)) NBTCompound(raw) else throw UnsupportedTypeException(raw)

    }

    operator fun set(node: String, value: NBTData?) = value?.let { put(node, it) }

    override fun putAll(from: Map<out String, NBTData>) = from.forEach { put(it.key, it.value) }

    override val keys: MutableSet<String> get() = sourceMap.keys

    override val size: Int get() = sourceMap.keys.size

    override fun clear() = sourceMap.clear()

    override fun isEmpty() = sourceMap.isEmpty()

    override fun containsValue(value: NBTData) = sourceMap.containsValue(value.getRaw())

    override fun containsKey(key: String) = sourceMap.containsKey(key)

    override val entries: MutableSet<MutableMap.MutableEntry<String, NBTData>> by lazy {
        object : AbstractMutableSet<MutableMap.MutableEntry<String, NBTData>>() {
            override val size get() = sourceMap.entries.size
            override fun add(element: MutableMap.MutableEntry<String, NBTData>) =
                sourceMap.entries.add(object : MutableMap.MutableEntry<String, Any> {
                    override val key get() = element.key
                    override val value get() = element.value.getRaw()
                    override fun setValue(newValue: Any) = element.setValue(NBTAdapter.adaptNms(newValue)).getRaw()
                })

            override fun iterator() = sourceMap.entries.iterator().let { source ->
                object : MutableIterator<MutableMap.MutableEntry<String, NBTData>> {
                    override fun hasNext() = source.hasNext()
                    override fun remove() = source.remove()
                    override fun next() = source.next().let {
                        object : MutableMap.MutableEntry<String, NBTData> {
                            override val key get() = it.key
                            override val value get() = NBTAdapter.adaptNms(it.value)
                            override fun setValue(newValue: NBTData) = NBTAdapter.adaptNms(it.setValue(newValue.getRaw()))
                        }
                    }
                }
            }
        }
    }

    override val values: MutableCollection<NBTData> by lazy {
        object : AbstractMutableCollection<NBTData>() {
            override fun add(element: NBTData) = sourceMap.values.add(element.getRaw())
            override val size get() = sourceMap.values.size
            override fun iterator() = sourceMap.values.iterator().let {
                object : MutableIterator<NBTData> {
                    override fun hasNext() = it.hasNext()
                    override fun next() = NBTAdapter.adaptNms(it.next())
                    override fun remove() = it.remove()
                }
            }
        }
    }

}
package cn.fd.ratziel.module.nbt

import java.util.concurrent.ConcurrentHashMap

/**
 * NBTCompound
 *
 * @author TheFloodDragon
 * @since 2024/3/15 19:28
 */
open class NBTCompound(
    /**
     * 源数据 - 采用 Java原生数据结构
     * [NBTCompound] 的所有操作都是在 [sourceMap] 上操作的
     */
    val sourceMap: MutableMap<String, Any>
) : NBTData(NBTType.COMPOUND), MutableMap<String, NBTData> {

    constructor() : this(ConcurrentHashMap())

    /**
     * 数据内容
     */
    override val content: Map<String, NBTData> get() = this

    /**
     * 获取数据
     * @param key 节点
     */
    override operator fun get(key: String): NBTData? = sourceMap[key]?.let { NBTAdapter.box(it) }

    /**
     * 写入数据
     * @param key 节点
     * @param value NBT数据
     */
    override fun put(key: String, value: NBTData) = sourceMap.put(key, value.content)?.let { NBTAdapter.box(it) }

    /**
     * 写入多组数据
     */
    override fun putAll(from: Map<out String, NBTData>) = from.forEach { sourceMap[it.key] = it.value.content }

    /**
     * 删除数据
     * @param key 节点
     */
    override fun remove(key: String) = sourceMap.remove(key)?.let { NBTAdapter.box(it) }

    /**
     * 克隆数据
     */
    override fun clone() = NBTCompound().also { new -> this.forEach { new[it.key] = it.value.clone() } }

    /**
     * 浅克隆数据
     */
    open fun cloneShallow() = NBTCompound().also { new -> new.putAll(this) }

    /**
     * 合并目标数据
     * @param replace 是否替换原有的数据 (false时不会 替换/删除 任何一个节点)
     */
    open fun merge(target: NBTCompound, replace: Boolean = true): NBTCompound {
        for ((key, targetValue) in target) {
            // 获取自身的数据
            val ownValue = this[key]
            // 自身数据不存在时, 直接替换为目标值
            if (ownValue == null) {
                this[key] = targetValue
            } else if (ownValue is NBTCompound && targetValue is NBTCompound) {
                // 同复合类型合并
                ownValue.merge(targetValue, replace)
            } else if (replace) {
                // 基础类型替换 (如果允许替换)
                this[key] = targetValue
            }
        }
        return this
    }

    /**
     * 合并目标数据 (浅合并)
     * @param replace 是否替换原有的标签
     */
    open fun mergeShallow(target: NBTCompound, replace: Boolean = true): NBTCompound {
        for ((key, value) in target) {
            // 如果当前数据中不存在, 或者允许替换
            if (!this.containsKey(key) || replace) {
                // 直接设置值
                this[key] = value
            }
        }
        return this
    }

    companion object {

        @JvmStatic
        fun of(map: Map<String, Any>): NBTCompound = NBTCompound(ConcurrentHashMap(map))

        @JvmStatic
        fun of(map: Map<String, NBTData>): NBTCompound = NBTCompound().apply { putAll(map) }

    }

    override fun clear() = sourceMap.clear()

    override val size get() = sourceMap.size

    override fun isEmpty() = sourceMap.isEmpty()

    override fun containsKey(key: String) = sourceMap.containsKey(key)

    override fun containsValue(value: NBTData) = sourceMap.containsValue(value.content)

    override val entries: MutableSet<MutableMap.MutableEntry<String, NBTData>>
        get() = object : AbstractMutableSet<MutableMap.MutableEntry<String, NBTData>>() {
            val ref = sourceMap.entries
            override val size get() = ref.size
            override fun add(element: MutableMap.MutableEntry<String, NBTData>) = ref.add(
                object : MutableMap.MutableEntry<String, Any> {
                    override val key get() = element.key
                    override val value get() = element.value.content
                    override fun setValue(newValue: Any) = element.setValue(NBTAdapter.box(newValue))
                })

            override fun iterator() = object : MutableIterator<MutableMap.MutableEntry<String, NBTData>> {
                val iRef = ref.iterator()
                override fun hasNext() = iRef.hasNext()
                override fun remove() = iRef.remove()
                override fun next() = iRef.next().let {
                    object : MutableMap.MutableEntry<String, NBTData> {
                        override val key get() = it.key
                        override val value get() = NBTAdapter.box(it.value)
                        override fun setValue(newValue: NBTData) = NBTAdapter.box(it.setValue(newValue.content))
                    }
                }
            }
        }

    override val keys get() = sourceMap.keys

    override val values
        get() = object : AbstractMutableCollection<NBTData>() {
            val ref = sourceMap.values
            override val size get() = ref.size
            override fun add(element: NBTData) = ref.add(element.content)
            override fun iterator() = object : MutableIterator<NBTData> {
                val iRef = ref.iterator()
                override fun hasNext() = iRef.hasNext()
                override fun next() = NBTAdapter.box(iRef.next())
                override fun remove() = iRef.remove()
            }
        }

}
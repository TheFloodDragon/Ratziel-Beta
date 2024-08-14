package cn.fd.ratziel.module.nbt

import java.util.concurrent.ConcurrentHashMap

/**
 * NBTCompound
 *
 * @author TheFloodDragon
 * @since 2024/3/15 19:28
 */
open class NBTCompound(override val content: MutableMap<String, NBTData>) : NBTData(NBTType.COMPOUND), MutableMap<String, NBTData> by content {

    constructor() : this(ConcurrentHashMap())

    /**
     * 克隆数据
     */
    override fun clone() = NBTCompound().also { new -> this.content.forEach { new.put(it.key, it.value.clone()) } }

    /**
     * 浅克隆数据
     */
    open fun cloneShallow() = NBTCompound().also { new -> new.putAll(this.content) }

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
        for ((key, value) in target.content) {
            // 如果当前数据中不存在, 或者允许替换
            if (!content.containsKey(key) || replace) {
                // 直接设置值
                content[key] = value
            }
        }
        return this
    }

    companion object {

        @JvmStatic
        fun of(map: Map<String, NBTData>): NBTCompound = NBTCompound(ConcurrentHashMap(map))

    }

}
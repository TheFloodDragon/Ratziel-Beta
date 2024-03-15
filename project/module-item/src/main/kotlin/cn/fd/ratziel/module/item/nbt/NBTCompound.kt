package cn.fd.ratziel.module.item.nbt

import cn.fd.ratziel.core.exception.UnsupportedTypeException
import java.util.function.Function

/**
 * NBTCompound
 *
 * @author TheFloodDragon
 * @since 2024/3/15 19:28
 */
@Suppress("UNCHECKED_CAST")
class NBTCompound(rawData: Any) : NBTData(rawData, NBTType.COMPOUND) {

    constructor() : this(new())

    init {
        if (!isOwnNmsClass(rawData::class.java)) throw UnsupportedTypeException(rawData)
    }

    internal val sourceMap get() = NMSUtil.NtCompound.sourceField.get(data) as HashMap<String, Any>

    val content: Map<String, NBTData> get() = buildMap { sourceMap.forEach { put(it.key, NBTConverter.convert(it.value)) } }

    /**
     * 获取数据
     * @param node 节点
     */
    operator fun get(node: String): NBTData? = sourceMap[node]?.let { NBTCompound(it) }

    /**
     * 写入数据
     * @param node 节点
     * @param value NBT数据
     */
    fun put(node: String, value: NBTData) = this.apply { sourceMap[node] = value.getData() }

    operator fun set(node: String, value: NBTData?) = value?.let { put(node, it) }

    fun computeIfAbsent(node: String, function: Function<String, NBTData>) = sourceMap.computeIfAbsent(node, function)

    /**
     * 克隆数据
     */
    fun clone() = this.apply {
        data = NMSUtil.NtCompound.methodClone.invoke(data)!!
    }

    /**
     * 合并目标数据
     * @param replace 是否替换原有的标签
     */
    fun merge(target: NBTCompound, replace: Boolean = true): NBTCompound = this.apply {
        target.sourceMap.forEach { (key, targetValue) ->
            val ownValue = this.sourceMap[key]
            // 如果当前NBT数据中存在, 且不允许替换, 则直接跳出循环
            if (ownValue != null && !replace) return@forEach
            // 反则设置值 (复合类型时递归)
            if (isOwnNmsClass(targetValue::class.java)) {
                // 判断当前值类型 (若非复合类型,则替换,此时目标值是复合类型的)
                val value: NBTCompound? = ownValue?.takeIf { isOwnNmsClass(it::class.java) }?.let { NBTCompound(it) }
                this.sourceMap[key] = (value ?: NBTCompound()).merge(NBTCompound(targetValue), replace).getData()
            } else this.sourceMap[key] = targetValue
        }
    }

    companion object {

        fun new() = new(HashMap())

        fun new(map: HashMap<String, Any>) = NMSUtil.NtCompound.constructor.instance(map)!!

        fun isOwnNmsClass(clazz: Class<*>) = NMSUtil.NtCompound.nmsClass.isAssignableFrom(clazz::class.java)

    }

}
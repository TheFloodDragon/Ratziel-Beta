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
     * 删除数据
     */
    fun remove(node: String) = this.apply { sourceMap.remove(node) }

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

    /**
     * 深度获取NBT数据
     */
    object DeepVisitor {
        /**
         * 深度操作分层符
         */
        const val DEEP_SEPARATION = "."

        /**
         * 顶级节点符号
         */
        const val APEX_NODE_SIGN = "!"

        /**
         * 列表索引标识符
         */
        const val LIST_INDEX_START = "["

        const val LIST_INDEX_END = "]"

        /**
         * 深度获取 TODO Finish This
         */
        fun getDeep(data: NBTCompound, node: String): NBTData? = data.run {
            if (node == APEX_NODE_SIGN) this
            else getDeepWith(this, node, false) { it[node.substringAfterLast(DEEP_SEPARATION)] }
        }

        /**
         * 深度写入
         */
        fun putDeep(data: NBTCompound, node: String, value: NBTData) = data.apply {
            if (node == APEX_NODE_SIGN) return@apply
            else getDeepWith(this, node, true) {
                supportList(node.substringAfterLast(DEEP_SEPARATION)) { pair ->
                    it.put(
                        pair.first, value = if (pair.second == null) value
                        else (it[pair.first] as? NBTList ?: NBTList()).apply { setCreatable(pair.second!!, value) }
                    )
                }
            }
        }

        /**
         * 深度删除 TODO Finish This
         */
        fun removeDeep(data: NBTCompound, node: String) = data.apply {
            if (node == APEX_NODE_SIGN) return@apply
            else getDeepWith(this, node, false) { it.remove(node.substringAfterLast(DEEP_SEPARATION)) }
        }

        /**
         * 针对"深度方法"的重复代码做出的优化
         */
        fun getDeepWith(data: NBTCompound, node: String, create: Boolean, action: (NBTCompound) -> Any?): NBTData? = data.apply {
            // 分割节点 (丢弃最后一层)
            val keys = node.split(DEEP_SEPARATION).dropLast(1)
            // 找到的标签
            var find: NBTCompound = this
            // 遍历各级节点
            for (element in keys) {
                var next = find[element] // 下一级节点
                if (next == null) {
                    if (create) {
                        next = NBTCompound(new())
                        find[element] = next
                    } else return null
                }
                // 如果下一级节点还是复合标签,则代表可以继续获取
                if (next is NBTCompound) find = next else return null
            }
            return action(find) as? NBTData
        }

        /**
         * 分割节点以获取索引值
         */
        private fun splitListOrNull(node: String): Pair<String, Int?> {
            val nodeName = node.substringBeforeLast(LIST_INDEX_START)
            val index = node.takeIf { it.endsWith(LIST_INDEX_END) }?.run {
                substring(
                    lastIndexOf(LIST_INDEX_START) + LIST_INDEX_START.length,
                    lastIndexOf(LIST_INDEX_END)
                ).toInt()
            }
            return nodeName to index
        }

        private fun <R> supportList(node: String, action: (Pair<String, Int?>) -> R) = splitListOrNull(node).let(action)

    }

}
@file:Suppress("NOTHING_TO_INLINE")

package cn.fd.ratziel.module.nbt

import java.util.function.Consumer

/**
 * [DeepVisitor] 系列方法
 */
inline fun NBTCompound.getDeep(node: String) = DeepVisitor.getDeep(this, node)
inline fun NBTCompound.putDeep(node: String, value: NBTData, checkList: Boolean = true) = DeepVisitor.putDeep(this, node, value, checkList)
inline fun NBTCompound.removeDeep(node: String) = DeepVisitor.removeDeep(this, node)

/**
 * 读取指定类型的数据
 */
@Deprecated("Will be removed")
inline fun <reified T : NBTData> NBTCompound.read(nodes: String, action: Consumer<T>) = (this.read(nodes) as? T)?.let { action.accept(it) }

/**
 * 读取数据
 */
@Deprecated("Will be removed")
inline fun NBTCompound.read(nodes: String): NBTData? = DeepVisitor.read(this, nodes)

/**
 * 写入数据
 */
@Deprecated("Will be removed")
inline fun NBTCompound.write(nodes: String, value: NBTData?) = value?.let { DeepVisitor.write(this, nodes, it) }

/**
 * 深度获取NBT数据
 * TODO 真是一坨shi
 */
object DeepVisitor {

    /**
     * 深度操作分层符
     */
    const val DEEP_SEPARATION = "."

    /**
     * 列表索引标识符
     */
    const val LIST_INDEX_START = "["

    const val LIST_INDEX_END = "]"

    @Deprecated("Will be removed")
    fun write(data: NBTCompound, nodes: String, value: NBTData) {
        if (nodes.contains(DEEP_SEPARATION)) {
            write(data, nodes.split(DEEP_SEPARATION).iterator(), value)
        } else data[nodes] = value
    }

    @Deprecated("Will be removed")
    fun read(data: NBTCompound, nodes: String): NBTData? {
        return if (nodes.contains(DEEP_SEPARATION)) {
            read(data, nodes.split(DEEP_SEPARATION))
        } else data[nodes]
    }

    @Deprecated("Will be removed")
    fun write(data: NBTCompound, nodes: Iterator<String>, value: NBTData) {
        var find = data
        while (nodes.hasNext()) {
            val node = nodes.next()
            if (nodes.hasNext()) {
                find = find.computeIfAbsent(node) { NBTCompound() } as? NBTCompound ?: return
            } else find[node] = value
        }
    }

    @Deprecated("Will be removed")
    fun read(data: NBTData, nodes: Iterable<String>): NBTData? {
        var find: NBTData = data
        for (n in nodes) {
            find = (data as? NBTCompound)?.get(n) ?: return null
        }
        return find
    }

    fun setSafely(data: NBTList, index: Int, value: NBTData) {
        if (index == data.size) data.add(value) else data.set(index, value)
    }

    // 开启类型检查, 列表不为空情况下的类型不匹配
    fun setSafely(data: NBTList, index: Int, value: NBTData, typeCheck: Boolean) {
        if (typeCheck && !data.isEmpty() && data[0].type != value.type) error("It's not allowed to set a ${value.type} data in this list.")
        else setSafely(data, index, value)
    }

    /**
     * 深度获取
     */
    fun getDeep(data: NBTCompound, node: String): NBTData? = data.run {
        getDeepWith(this, node, false) { cpd ->
            supportList(node.substringAfterLast(DEEP_SEPARATION)) { (nodeName, index) ->
                if (index == null) cpd[nodeName]
                else (cpd[nodeName] as? NBTList)?.let { it[index] }
            }
        }
    }

    /**
     * 深度写入
     */
    fun putDeep(data: NBTCompound, node: String, value: NBTData, checkList: Boolean) = data.apply {
        getDeepWith(this, node, true) { cpd ->
            supportList(node.substringAfterLast(DEEP_SEPARATION)) { (nodeName, index) ->
                if (index == null) cpd[nodeName] = value
                else ((cpd[nodeName] ?: NBTList().also { cpd[nodeName] = it }) as? NBTList)?.also { setSafely(it, index, value, checkList) }
            }
        }
    }

    /**
     * 深度删除
     */
    fun removeDeep(data: NBTCompound, node: String) = data.apply {
        getDeepWith(this, node, false) { cpd ->
            supportList(node.substringAfterLast(DEEP_SEPARATION)) { (nodeName, index) ->
                if (index == null) cpd.remove(nodeName)
                else (cpd[nodeName] as? NBTList)?.apply { removeAt(index) }
            }
        }
    }

    /**
     * 针对"深度方法"的重复代码做出的优化
     */
    fun getDeepWith(data: NBTCompound, node: String, create: Boolean, action: (NBTCompound) -> Any?): NBTData? = data.apply {
        if (!node.contains(DEEP_SEPARATION)) return action(data) as? NBTData
        // 分割节点 (丢弃最后一层)
        val keys = node.split(DEEP_SEPARATION).dropLast(1)
        // 找到的标签
        var find: NBTCompound = this
        // 遍历各级节点
        for (element in keys) {
            var next = find.getDeep(element) // 下一级节点
            if (next == null) {
                if (create) {
                    next = NBTCompound()
                    find.putDeep(element, next)
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
        if (node.contains(LIST_INDEX_START) && node.contains(LIST_INDEX_END)) {
            val nodeName = node.substringBeforeLast(LIST_INDEX_START)
            val index = node.substring(node.lastIndexOf(LIST_INDEX_START) + LIST_INDEX_START.length, node.lastIndexOf(LIST_INDEX_END))
            return nodeName to index.toInt()
        } else return node to null
    }

    private fun <R> supportList(node: String, action: (Pair<String, Int?>) -> R) = splitListOrNull(node).let(action)

}
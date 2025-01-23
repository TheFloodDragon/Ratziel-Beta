@file:Suppress("NOTHING_TO_INLINE")

package cn.fd.ratziel.module.nbt

import cn.altawk.nbt.tag.NbtCompound
import cn.altawk.nbt.tag.NbtList
import cn.altawk.nbt.tag.NbtTag
import java.util.function.Consumer

/**
 * [DeepVisitor] 系列方法
 */
inline fun NbtCompound.getDeep(node: String) = DeepVisitor.getDeep(this, node)
inline fun NbtCompound.putDeep(node: String, value: NbtTag, checkList: Boolean = true) = DeepVisitor.putDeep(this, node, value, checkList)
inline fun NbtCompound.removeDeep(node: String) = DeepVisitor.removeDeep(this, node)

/**
 * 读取指定类型的数据
 */
@Deprecated("Will be removed")
inline fun <reified T : NbtTag> NbtCompound.read(nodes: String, action: Consumer<T>) = (this.read(nodes) as? T)?.let { action.accept(it) }

/**
 * 读取数据
 */
@Deprecated("Will be removed")
inline fun NbtCompound.read(nodes: String): NbtTag? = DeepVisitor.read(this, nodes)

/**
 * 写入数据
 */
@Deprecated("Will be removed")
inline fun NbtCompound.write(nodes: String, value: NbtTag?) = value?.let { DeepVisitor.write(this, nodes, it) }

/**
 * 深度获取NBT数据
 * TODO 真是一坨shi
 */
@Deprecated("Will be removed")
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
    fun write(data: NbtCompound, nodes: String, value: NbtTag) {
        if (nodes.contains(DEEP_SEPARATION)) {
            write(data, nodes.split(DEEP_SEPARATION).iterator(), value)
        } else data[nodes] = value
    }

    @Deprecated("Will be removed")
    fun read(data: NbtCompound, nodes: String): NbtTag? {
        return if (nodes.contains(DEEP_SEPARATION)) {
            read(data, nodes.split(DEEP_SEPARATION))
        } else data[nodes]
    }

    @Deprecated("Will be removed")
    fun write(data: NbtCompound, nodes: Iterator<String>, value: NbtTag) {
        var find = data
        while (nodes.hasNext()) {
            val node = nodes.next()
            if (nodes.hasNext()) {
                find = find.computeIfAbsent(node) { NbtCompound() } as? NbtCompound ?: return
            } else find[node] = value
        }
    }

    @Deprecated("Will be removed")
    fun read(data: NbtTag, nodes: Iterable<String>): NbtTag? {
        var find: NbtTag = data
        for (n in nodes) {
            find = (data as? NbtCompound)?.get(n) ?: return null
        }
        return find
    }

    fun setSafely(data: NbtList<NbtTag>, index: Int, value: NbtTag) {
        if (index == data.size) data.add(value) else data[index] = value
    }

    // 开启类型检查, 列表不为空情况下的类型不匹配
    fun setSafely(data: NbtList<NbtTag>, index: Int, value: NbtTag, typeCheck: Boolean) {
        if (typeCheck && !data.isEmpty() && data.elementType != value.type) error("It's not allowed to set a ${value.type} data in this list.")
        else setSafely(data, index, value)
    }

    /**
     * 深度获取
     */
    fun getDeep(data: NbtCompound, node: String): NbtTag? = data.run {
        getDeepWith(this, node, false) { cpd ->
            supportList(node.substringAfterLast(DEEP_SEPARATION)) { (nodeName, index) ->
                if (index == null) cpd[nodeName]
                else (cpd[nodeName] as? NbtList<*>)?.let { it[index] }
            }
        }
    }

    /**
     * 深度写入
     */
    fun putDeep(data: NbtCompound, node: String, value: NbtTag, checkList: Boolean) = data.apply {
        getDeepWith(this, node, true) { cpd ->
            supportList(node.substringAfterLast(DEEP_SEPARATION)) { (nodeName, index) ->
                @Suppress("UNCHECKED_CAST")
                if (index == null) cpd[nodeName] = value
                else ((cpd[nodeName] ?: NbtList<NbtTag>().also { cpd[nodeName] = it }) as? NbtList<NbtTag>)?.also { setSafely(it, index, value, checkList) }
            }
        }
    }

    /**
     * 深度删除
     */
    fun removeDeep(data: NbtCompound, node: String) = data.apply {
        getDeepWith(this, node, false) { cpd ->
            supportList(node.substringAfterLast(DEEP_SEPARATION)) { (nodeName, index) ->
                if (index == null) cpd.remove(nodeName)
                else (cpd[nodeName] as? NbtList<*>)?.apply { removeAt(index) }
            }
        }
    }

    /**
     * 针对"深度方法"的重复代码做出的优化
     */
    fun getDeepWith(data: NbtCompound, node: String, create: Boolean, action: (NbtCompound) -> Any?): NbtTag? = data.apply {
        if (!node.contains(DEEP_SEPARATION)) return action(data) as? NbtTag
        // 分割节点 (丢弃最后一层)
        val keys = node.split(DEEP_SEPARATION).dropLast(1)
        // 找到的标签
        var find: NbtCompound = this
        // 遍历各级节点
        for (element in keys) {
            var next = find.getDeep(element) // 下一级节点
            if (next == null) {
                if (create) {
                    next = NbtCompound()
                    find.putDeep(element, next)
                } else return null
            }
            // 如果下一级节点还是复合标签,则代表可以继续获取
            if (next is NbtCompound) find = next else return null
        }
        return action(find) as? NbtTag
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
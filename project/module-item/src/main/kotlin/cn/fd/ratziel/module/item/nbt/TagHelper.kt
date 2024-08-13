@file:Suppress("NOTHING_TO_INLINE")

package cn.fd.ratziel.module.item.nbt

import cn.fd.ratziel.module.item.api.ItemNode

/**
 * TagHelper - 用于读取/写入 [NBTCompound] 的节点信息
 *
 * @author TheFloodDragon
 * @since 2024/8/12 19:41
 */
object TagHelper {

    @JvmStatic
    fun readCreatable(source: NBTCompound, tailNode: ItemNode) = readCreatable(source, unfold(tailNode))

    @JvmStatic
    fun write(source: NBTCompound, tailNode: ItemNode, value: NBTData) = write(source, unfold(tailNode), value)

    @JvmStatic
    fun read(source: NBTCompound, tailNode: ItemNode) = read(source, unfold(tailNode))

    /**
     * 读取 [NBTCompound]
     * 如果获取到的 [find] 为空则写入 空的[NBTCompound]
     */
    @JvmStatic
    fun readCreatable(source: NBTCompound, iterator: Iterator<String>): NBTCompound {
        var find = source
        for (node in iterator) {
            find = find.compute(node) { _, v -> if (v is NBTCompound) v else NBTCompound() } as NBTCompound
        }
        return find
    }

    /**
     * 通过节点写入 [NBTData]
     * 如果获取到的 [find] 为空则写入 空的[NBTCompound]
     */
    @JvmStatic
    fun write(source: NBTCompound, iterator: Iterator<String>, value: NBTData) {
        var find: NBTCompound = source
        while (iterator.hasNext()) {
            val node = iterator.next()
            // 可以继续往下
            if (iterator.hasNext())
                find = find.compute(node) { _, v -> if (v is NBTCompound) v else NBTCompound() } as NBTCompound
            // 如果是最后一个则直接设置
            else find[node] = value
        }
    }

    /**
     * 通过节点获取 [NBTData]
     * 值不存在时返回空
     */
    @JvmStatic
    fun read(source: NBTCompound, iterator: Iterator<String>): NBTData? {
        var find: NBTCompound = source
        while (iterator.hasNext()) {
            val next = find[iterator.next()]
            // 如果是最后一个则直接返回
            if (!iterator.hasNext()) return next
            // 还可以继续往下找
            else if (next != null && next is NBTCompound) find = next
            // 无法继续寻找
            else return null
        }
        // 只有当 iterable 没有元素的时候, 才执行到这, 直接返回 source
        return find
    }

    /**
     * 展开物品节点 (从上往下的顺序, 不包括[ItemNode.ROOT])
     * @return 物品节点迭代器
     */
    @JvmStatic
    fun unfold(tailNode: ItemNode): Iterator<String> {
        val nodeList = buildList {
            var node: ItemNode = tailNode
            while (node != ItemNode.ROOT) {
                add(node.name)
                node = node.parent
            }
        }
        // 返回迭代器并调换Next和Previous来做到反转的效果
        return object : Iterator<String> {
            val iterator = nodeList.listIterator(nodeList.lastIndex)
            override fun hasNext() = iterator.hasPrevious()
            override fun next() = iterator.previous()
        }
    }

}

/**
 * 读取 [String]
 */
inline fun NBTCompound.readString(node: String): String? = this[node]?.content as? String

/**
 * 读取 [Int]
 */
inline fun NBTCompound.readInt(node: String): Int? = this[node]?.content as? Int

/**
 * 读取 [Boolean]
 */
inline fun NBTCompound.readBoolean(node: String): Boolean? = (this[node]?.content as? Byte)?.let { NBTByte.adaptOrNull(it) }
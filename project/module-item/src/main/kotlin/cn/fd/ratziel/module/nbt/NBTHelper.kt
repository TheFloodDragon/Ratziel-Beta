package cn.fd.ratziel.module.nbt

import cn.altawk.nbt.tag.NbtByte
import cn.altawk.nbt.tag.NbtCompound
import cn.altawk.nbt.tag.NbtTag
import cn.fd.ratziel.module.item.api.ItemNode

/**
 * NBTHelper
 *
 * @author TheFloodDragon
 * @since 2024/8/12 19:41
 */
object NBTHelper : NMSUtil by NMSUtil.INSTANCE {

    @JvmStatic
    fun readCreatable(source: NbtCompound, tailNode: ItemNode) = readCreatable(source, unfold(tailNode))

    @JvmStatic
    fun write(source: NbtCompound, tailNode: ItemNode, value: NbtTag) = write(source, unfold(tailNode), value)

    @JvmStatic
    fun read(source: NbtCompound, tailNode: ItemNode) = read(source, unfold(tailNode))

    /**
     * 读取 [NbtCompound]
     * 如果获取到的 [find] 为空则写入 空的[NbtCompound]
     */
    @JvmStatic
    fun readCreatable(source: NbtCompound, iterator: Iterator<String>): NbtCompound {
        var find = source
        for (node in iterator) {
            find = find.compute(node) { _, v -> if (v is NbtCompound) v else NbtCompound() } as NbtCompound
        }
        return find
    }

    /**
     * 通过节点写入 [NbtTag]
     * 如果获取到的 [find] 为空则写入 空的[NbtCompound]
     */
    @JvmStatic
    fun write(source: NbtCompound, iterator: Iterator<String>, value: NbtTag) {
        var find: NbtCompound = source
        while (iterator.hasNext()) {
            val node = iterator.next()
            // 可以继续往下
            if (iterator.hasNext())
                find = find.compute(node) { _, v -> if (v is NbtCompound) v else NbtCompound() } as NbtCompound
            // 如果是最后一个则直接设置
            else find[node] = value
        }
    }

    /**
     * 通过节点获取 [NbtTag]
     * 值不存在时返回空
     */
    @JvmStatic
    fun read(source: NbtCompound, iterator: Iterator<String>): NbtTag? {
        var find: NbtCompound = source
        while (iterator.hasNext()) {
            val next = find[iterator.next()]
            // 如果是最后一个则直接返回
            if (!iterator.hasNext()) return next
            // 还可以继续往下找
            else if (next != null && next is NbtCompound) find = next
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
            val iterator = nodeList.listIterator(nodeList.size)
            override fun hasNext() = iterator.hasPrevious()
            override fun next() = iterator.previous()
        }
    }

}

/**
 * 读取 [String]
 */
fun NbtCompound.readString(node: String): String? = this[node]?.content as? String

/**
 * 读取 [Int]
 */
fun NbtCompound.readInt(node: String): Int? = this[node]?.content as? Int

/**
 * 读取 [Byte]
 */
fun NbtCompound.readByte(node: String): Byte? = this[node]?.content as? Byte

/**
 * 读取 [Boolean]
 */
fun NbtCompound.readBoolean(node: String): Boolean? = readByte(node)?.let { NbtByte(it).toBoolean() }
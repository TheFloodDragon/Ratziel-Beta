package cn.fd.ratziel.module.item.util

import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.ItemNode
import cn.fd.ratziel.module.item.api.ItemTransformer
import cn.fd.ratziel.module.item.impl.SimpleItemData
import cn.fd.ratziel.module.item.nbt.NBTCompound
import cn.fd.ratziel.module.item.nbt.NBTData

/**
 * ComponentUtil
 *
 * @author TheFloodDragon
 * @since 2024/6/24 17:15
 */
object ComponentUtil {

    /**
     * 通过 [transformer] 将 [component] 转化成 [ItemData]
     */
    fun <T> toData(data: ItemData.Mutable, component: T, transformer: ItemTransformer<T>): ItemData.Mutable {
        transformer.transform(data, component) // 转换底层数据
        val newTag = NBTCompound() // 创建新NBT
        setByNode(newTag, transformer.node, data.tag) // 设置新NBT
        return SimpleItemData(data.material, newTag, data.amount)
    }

    /**
     * 通过 [transformer] 将 [data] 转化成 物品组件
     */
    fun <T> toComponent(data: ItemData, transformer: ItemTransformer<T>): T {
        val find = findByNode(data.tag, transformer.node)
        return transformer.detransform(SimpleItemData(data.material, find, data.amount))
    }

    fun findByNode(source: NBTCompound, tailNode: ItemNode) = findByNode(source, fold(tailNode))

    fun findByNode(source: NBTCompound, nodes: Iterable<ItemNode>): NBTCompound {
        var find = source
        for (node in nodes) {
            find = find.computeIfAbsent(node.name) { NBTCompound() } as NBTCompound
        }
        return find
    }

    fun findByNodeOrNull(source: NBTCompound, nodes: Iterable<ItemNode>): NBTCompound? {
        var find = source
        for (node in nodes) {
            find = (find[node.name] as? NBTCompound) ?: return null
        }
        return find
    }

    fun findByNodeOrNull(source: NBTCompound, tailNode: ItemNode): NBTCompound? = findByNodeOrNull(source, fold(tailNode))

    fun setByNode(source: NBTCompound, tailNode: ItemNode, data: NBTData) {
        // 根节点处理
        if (tailNode == ItemNode.ROOT) {
            // 若同为复合类型, 则进行浅合并 (替换)
            if (data is NBTCompound) source.mergeShallow(data, true)
        } else {
            val node = tailNode.parent // 去掉最后一层节点
            val find = findByNode(source, node) // 寻找节点
            find[tailNode.name] = data // 设置最后一层
        }
    }

    fun fold(tailNode: ItemNode) = buildList {
        var node: ItemNode = tailNode
        while (node != ItemNode.ROOT) {
            add(node)
            node = node.parent
        }
    }.reversed()

}
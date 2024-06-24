package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.ItemNode
import cn.fd.ratziel.module.item.api.ItemTransformer
import cn.fd.ratziel.module.item.impl.TheItemData
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
    fun <T> toData(component: T, transformer: ItemTransformer<T>): ItemData {
        val data = transformer.transform(component) // 获取底层数据
        val newTag = NBTCompound() // 创建新NBT
        setByNode(newTag, transformer.node, data.tag) // 设置新NBT
        return TheItemData(data.material, newTag, data.amount)
    }

    /**
     * 通过 [transformer] 将 [data] 转化成 物品组件
     */
    fun <T> toComponent(data: ItemData, transformer: ItemTransformer<T>): T {
        val find = findByNode(data.tag, transformer.node)
        return transformer.detransform(TheItemData(data.material, find, data.amount))
    }

    fun findByNode(source: NBTCompound, tailNode: ItemNode) = findByNode(source, fold(tailNode))

    fun findByNode(source: NBTCompound, nodes: Iterable<ItemNode>): NBTCompound {
        var find = source
        for (node in nodes) {
            find = find.computeIfAbsent(node.name) { NBTCompound() } as NBTCompound
        }
        return find
    }

    fun setByNode(source: NBTCompound, tailNode: ItemNode, data: NBTData) {
        val node = tailNode.parent ?: return // 去掉最后一层节点, 若为尾节点顶级节点, 则直接返回
        val find = findByNode(source, node) // 寻找节点
        find[tailNode.name] = data // 设置最后一层
    }

    fun fold(tailNode: ItemNode) = buildList {
        var node: ItemNode = tailNode
        while (node.parent != null) {
            add(node)
            node = node.parent ?: break
        }
    }.reversed()

}
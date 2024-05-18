package cn.fd.ratziel.module.item.util

import cn.fd.ratziel.module.item.api.ItemComponent
import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.ItemMaterial
import cn.fd.ratziel.module.item.api.ItemNode
import cn.fd.ratziel.module.item.impl.ItemDataImpl
import cn.fd.ratziel.module.item.nbt.NBTCompound
import cn.fd.ratziel.module.item.nbt.NBTData
import java.util.function.Consumer

fun ItemComponent.transfer(data: ItemData) = ComponentUtil.transfer(this, data)

fun ItemComponent.accept(data: ItemData) = ComponentUtil.accept(this, data)

/**
 * 转换[NBTData], 若成功转换(不为空), 则执行 [action]
 */
inline fun <reified T : NBTData> NBTData?.castThen(action: Consumer<T>) = (this as? T)?.let { action.accept(it) }

/**
 * ComponentUtil
 *
 * @author TheFloodDragon
 * @since 2024/5/18 08:15
 */
object ComponentUtil {

    /**
     * 将 [ItemComponent] 的数据转换并转移到 [data]
     */
    fun transfer(component: ItemComponent, data: ItemData) {
        // 获取数据
        val find = findByNode(data.tag, component.getNode())
        // 创建供物品组件使用的数据
        val transmittedData = object : ItemData {
            override var material: ItemMaterial
                get() = data.material
                set(value) {
                    data.material = value
                }
            override var tag: NBTCompound = find
                set(value) {
                    field = value
                    setByNode(data.tag, component.getNode(), value)
                }
            override var amount: Int
                get() = data.amount
                set(value) {
                    data.amount = value
                }
        }
        // 应用
        component.transform(transmittedData)
    }

    /**
     * 从 [data] 的中接受数据并应用到 [component]
     * 禁止改写 [data]
     */
    fun accept(component: ItemComponent, data: ItemData) {
        val find = findByNode(data.tag, component.getNode())
        component.detransform(ItemDataImpl(data.material, find, data.amount))
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
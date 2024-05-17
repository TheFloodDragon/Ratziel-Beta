package cn.fd.ratziel.module.item.util

import cn.fd.ratziel.module.item.api.ItemComponent
import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.ItemMaterial
import cn.fd.ratziel.module.item.api.NodeDistributor
import cn.fd.ratziel.module.item.impl.ItemDataImpl
import cn.fd.ratziel.module.item.nbt.NBTCompound
import cn.fd.ratziel.module.item.nbt.NBTData
import java.util.function.Consumer

/**
 * 将[ItemComponent]转化成[NBTCompound]并应用到[data]上
 * TODO 好大一坨
 */
fun ItemComponent.applyTo(data: ItemData) = this.transform(ItemDataImpl(nbt=data.nbt.findByNode(this.getNode())))

fun NBTCompound.findByNode(distributor: NodeDistributor): NBTCompound {
    var find = this
    var node = distributor
    while (node.parent != null) {
        find = find[node.name] as? NBTCompound ?: NBTCompound().also { find[node.name] = it }
        node = node.parent ?: break
    }
    return find
}

/**
 * 转换[NBTData], 若成功转换(不为空), 则执行 [action]
 */
inline fun <reified T : NBTData> NBTData?.castThen(action: Consumer<T>) = (this as? T)?.let { action.accept(it) }
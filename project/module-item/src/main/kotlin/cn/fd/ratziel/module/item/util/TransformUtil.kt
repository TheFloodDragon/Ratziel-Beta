package cn.fd.ratziel.module.item.util

import cn.fd.ratziel.module.item.api.ItemComponent
import cn.fd.ratziel.module.item.api.NodeDistributor
import cn.fd.ratziel.module.item.nbt.NBTCompound
import cn.fd.ratziel.module.item.nbt.NBTData
import java.util.function.Consumer

/**
 * 将[ItemComponent]转化成[NBTCompound]并应用到[source]上
 */
fun ItemComponent.applyTo(source: NBTCompound = NBTCompound(), replace: Boolean = true) =
    source.also { it.findByNode(this.getNode()).merge(this.transform().nbt, replace) }

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
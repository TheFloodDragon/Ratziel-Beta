package cn.fd.ratziel.item.impl

import cn.fd.ratziel.core.util.futureFactory
import cn.fd.ratziel.item.meta.VItemMeta
import cn.fd.ratziel.item.util.RefItemMeta
import cn.fd.ratziel.item.util.emptyCraftItemMeta
import org.bukkit.inventory.meta.ItemMeta
import taboolib.module.nms.NMSItemTag
import taboolib.module.nms.nmsProxy

/**
 * DefaultItemGenerator
 *
 * @author TheFloodDragon
 * @since 2023/10/28 13:56
 */
class DefaultItemGenerator {

    fun build(vm: VItemMeta): ItemMeta {
        // 基础信息构建
        val itemMeta = (emptyCraftItemMeta() as ItemMeta).also { meta ->
            futureFactory {
                submit { vm.display.build(meta) }
                submit { vm.characteristic.build(meta) }
                submit {
                    // 获取NMS标签
                    val nmsTag = nmsProxy<NMSItemTag>().itemTagToNMSCopy(vm.nbt)
                    println("NMSTag: $nmsTag")
                    // 应用
                    RefItemMeta.applyToItem(meta, nmsTag)
                }
            }.waitForAll()
        }

        return itemMeta
    }

}
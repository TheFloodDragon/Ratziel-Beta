package cn.fd.ratziel.item.impl

import cn.fd.ratziel.core.util.futureFactory
import cn.fd.ratziel.item.api.builder.ItemGenerator
import cn.fd.ratziel.item.meta.VItemMeta
import cn.fd.ratziel.item.util.meta.emptyCraftItemMeta
import org.bukkit.inventory.meta.ItemMeta

/**
 * DefaultItemGenerator
 *
 * @author TheFloodDragon
 * @since 2023/10/28 13:56
 */
class DefaultItemGenerator : ItemGenerator {

    fun build(vm: VItemMeta): ItemMeta = (emptyCraftItemMeta() as ItemMeta).also { meta ->
        // 基础信息构建
        futureFactory {
            submit { vm.display.build(meta) }
            submit { vm.characteristic.build(meta) }
        }.waitForAll()
    }

}
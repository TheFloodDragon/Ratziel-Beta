package cn.fd.ratziel.module.item.impl

import cn.fd.ratziel.core.util.futureFactory
import cn.fd.ratziel.module.item.api.builder.ItemGenerator
import cn.fd.ratziel.module.item.item.meta.VItemMeta
import cn.fd.ratziel.module.item.util.meta.emptyCraftItemMeta
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
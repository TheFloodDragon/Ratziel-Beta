package cn.fd.ratziel.module.item.impl

import cn.fd.ratziel.core.util.futureFactory
import cn.fd.ratziel.module.item.api.builder.ItemGenerator
import cn.fd.ratziel.module.item.item.meta.VItemMeta
import cn.fd.ratziel.module.item.util.nbt.NBTTag
import cn.fd.ratziel.module.item.util.nbt.toNMS
import cn.fd.ratziel.module.item.util.ref.RefItemMeta
import org.bukkit.inventory.meta.ItemMeta

/**
 * DefaultItemGenerator
 *
 * @author TheFloodDragon
 * @since 2023/10/28 13:56
 */
class DefaultItemGenerator : ItemGenerator {

    fun build(vm: VItemMeta): ItemMeta = NBTTag().also { tag ->
        // 基础信息构建
        futureFactory {
            newAsync { vm.display.build(tag) }
            newAsync { vm.characteristic.build(tag) }
        }.waitForAll()
    }.let { RefItemMeta.new(it.toNMS()) as ItemMeta }

}
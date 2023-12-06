package cn.fd.ratziel.module.itemengine.item.builder

import cn.fd.ratziel.core.function.futureFactoryAny
import cn.fd.ratziel.module.itemengine.api.builder.ItemGenerator
import cn.fd.ratziel.module.itemengine.item.meta.VItemMeta
import cn.fd.ratziel.module.itemengine.mapping.RefItemMeta
import cn.fd.ratziel.module.itemengine.nbt.NBTTag
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
        futureFactoryAny {
            newAsync { vm.display.build(tag) }
            newAsync { vm.characteristic.build(tag) }
        }.wait()
    }.let { RefItemMeta.new(it.getAsNmsNBT()) as ItemMeta }

}
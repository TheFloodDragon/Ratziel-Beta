package cn.fd.ratziel.module.itemengine.item.builder

import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.module.item.api.builder.ItemGenerator
import cn.fd.ratziel.module.item.api.builder.ItemStream
import cn.fd.ratziel.module.item.reflex.RefItemMeta
import cn.fd.ratziel.module.itemengine.item.meta.VItemMeta
import cn.fd.ratziel.module.itemengine.nbt.NBTTag
import cn.fd.ratziel.module.itemengine.util.applyFrom
import org.bukkit.inventory.meta.ItemMeta

/**
 * DefaultItemGenerator
 *
 * @author TheFloodDragon
 * @since 2023/10/28 13:56
 */
open class DefaultItemGenerator(override val origin: Element) : ItemGenerator {

    fun build(vm: VItemMeta): ItemMeta = NBTTag().also { tag ->
        // 基础信息构建
        vm.display.applyFrom(tag)
        vm.characteristic.applyFrom(tag)
        tag.merge(vm.nbt)
    }.let { RefItemMeta.new(it.getAsNmsNBT()) as ItemMeta }

    override val streams: Array<ItemStream> = TODO("Will be deleted")

}
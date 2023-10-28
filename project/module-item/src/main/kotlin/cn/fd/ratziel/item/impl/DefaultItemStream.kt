package cn.fd.ratziel.item.impl

import cn.fd.ratziel.bukkit.nbt.NBTTag
import cn.fd.ratziel.item.api.ItemStream
import cn.fd.ratziel.item.meta.VItemMeta
import cn.fd.ratziel.item.util.emptyCraftItemMeta
import org.bukkit.inventory.meta.ItemMeta

/**
 * DefaultItemStream
 *
 * @author TheFloodDragon
 * @since 2023/10/27 22:23
 */
@Deprecated("意义不明")
class DefaultItemStream(nbtTag: NBTTag = NBTTag()) : ItemStream(nbtTag) {

    fun build(vm: VItemMeta): ItemMeta {
        val itemMeta = (emptyCraftItemMeta() as ItemMeta).apply {
            vm.display.build(this)
            vm.characteristic.build(this)
        }

        return itemMeta
    }

}
package cn.fd.ratziel.item.meta

import cn.fd.ratziel.item.api.ItemMetadata
import cn.fd.ratziel.item.nms.ObcItemMeta
import org.bukkit.inventory.meta.ItemMeta
import taboolib.module.nms.ItemTag
import taboolib.module.nms.NMSItemTag
import taboolib.module.nms.nmsProxy

/**
 * VItemMeta
 *
 * @author TheFloodDragon
 * @since 2023/10/14 16:15
 */
open class VItemMeta(
    override var display: VItemDisplay = VItemDisplay(),
    override var characteristic: VItemCharacteristic = VItemCharacteristic(),
    override var durability: VItemDurability = VItemDurability(),
    override var nbt: ItemTag = ItemTag(),
) : ItemMetadata {

    fun toItemMeta(): ItemMeta {
        val tbTag = nbt.apply { display.applyTo(this) }
        val nmsTag = nmsProxy<NMSItemTag>().itemTagToNMSCopy(tbTag)
        println(nmsTag)
        val baseMeta = ObcItemMeta.createInstance(nmsTag) as ItemMeta
        val finalMeta = characteristic.applyTo(baseMeta, false)
        return finalMeta
    }

}
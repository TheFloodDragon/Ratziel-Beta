package cn.fd.ratziel.item.api.meta

import org.bukkit.inventory.meta.ItemMeta

/**
 * ItemMetaBuilder
 *
 * @author TheFloodDragon
 * @since 2023/10/27 22:18
 */
interface ItemMetaBuilder {

    /**
     * 构造(obc)ItemMeta
     */
    fun build(meta: ItemMeta)

}
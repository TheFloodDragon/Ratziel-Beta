package cn.fd.ratziel.module.itemengine.api.builder

import org.bukkit.inventory.meta.ItemMeta

/**
 * ItemMetaBuilder - 物品元数据构造器
 *
 * @author TheFloodDragon
 * @since 2023/10/27 22:18
 */
@Deprecated("Use ItemTagBuilder")
interface ItemMetaBuilder : ItemBuilder {

    /**
     * 构造(obc)ItemMeta
     */
    fun build(meta: ItemMeta)

}
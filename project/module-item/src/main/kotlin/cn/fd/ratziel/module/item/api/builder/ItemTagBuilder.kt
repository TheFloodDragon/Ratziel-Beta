package cn.fd.ratziel.module.item.api.builder

import taboolib.module.nms.ItemTag

/**
 * ItemTagBuilder - 物品标签构建器
 *
 * @author TheFloodDragon
 * @since 2023/11/11 15:44
 */
interface ItemTagBuilder {

    /**
     * 构建物品标签
     */
    fun build(tag: ItemTag)

}
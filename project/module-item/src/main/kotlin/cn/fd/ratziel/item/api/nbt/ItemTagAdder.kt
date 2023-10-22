package cn.fd.ratziel.item.api.nbt

import taboolib.module.nms.ItemTag

/**
 * ItemTagAdder
 *
 * @author TheFloodDragon
 * @since 2023/10/21 19:04
 */
interface ItemTagAdder {

    /**
     * 应用到物品标签中
     * @param source 原始物品标签
     */
    fun applyTo(source: ItemTag)

}
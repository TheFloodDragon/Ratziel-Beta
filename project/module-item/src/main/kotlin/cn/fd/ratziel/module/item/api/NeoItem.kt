package cn.fd.ratziel.module.item.api

import cn.fd.ratziel.module.item.api.service.ItemService

/**
 * NeoItem - 物品
 *
 * @author TheFloodDragon
 * @since 2024/5/2 21:55
 */
interface NeoItem {

    /**
     * 物品数据
     */
    val data: ItemData

    /**
     * 物品服务
     */
    val service: ItemService

    /**
     * 复制物品实例
     */
    fun clone(): NeoItem

}
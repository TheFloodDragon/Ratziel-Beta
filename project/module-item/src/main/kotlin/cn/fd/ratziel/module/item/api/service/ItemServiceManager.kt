package cn.fd.ratziel.module.item.api.service

import cn.fd.ratziel.Identifier


/**
 * ItemServiceManager - 物品服务管理器
 *
 * @author TheFloodDragon
 * @since 2024/5/4 10:01
 */
interface ItemServiceManager {

    /**
     * 获取物品的服务
     */
    operator fun get(identifier: Identifier): ItemService

    /**
     * 设置物品的服务
     */
    operator fun set(identifier: Identifier, value: ItemService)

}
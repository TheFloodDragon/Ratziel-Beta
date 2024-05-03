package cn.fd.ratziel.module.item.impl.service

import cn.fd.ratziel.module.item.api.ItemIdentifier
import cn.fd.ratziel.module.item.api.ItemService
import java.util.concurrent.ConcurrentHashMap

/**
 * ServiceManager - 物品服务管理器
 *
 * @author TheFloodDragon
 * @since 2024/5/3 14:41
 */
object ServiceManager {

    /**
     * 物品服务
     */
    val services: MutableMap<ItemIdentifier, ItemService> = ConcurrentHashMap()

    // TODO(实现)

}
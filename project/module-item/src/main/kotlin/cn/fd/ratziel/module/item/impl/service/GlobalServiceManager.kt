package cn.fd.ratziel.module.item.impl.service

import cn.fd.ratziel.module.item.api.ItemIdentifier
import cn.fd.ratziel.module.item.api.service.ItemService
import cn.fd.ratziel.module.item.api.service.ItemServiceManager
import cn.fd.ratziel.module.item.impl.ItemIdentifierImpl
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * GlobalServiceManager - 全局物品服务管理器
 *
 * @author TheFloodDragon
 * @since 2024/5/3 14:41
 */
object GlobalServiceManager : ItemServiceManager {

    /**
     * 物品服务组
     */
    val groups: MutableMap<ItemIdentifier, ItemService> = ConcurrentHashMap()

    /**
     * 获取物品的服务
     */
    override fun get(identifier: ItemIdentifier): ItemService {
        return groups.computeIfAbsent(identifier) { DefaultItemService(it) }
    }

    /**
     * 设置物品的服务
     */
    override fun set(identifier: ItemIdentifier, value: ItemService) {
        groups[identifier] = value
    }

    operator fun get(identifier: String) = get(ItemIdentifierImpl(identifier))

    operator fun get(identifier: UUID) = get(ItemIdentifierImpl(identifier))

    operator fun set(identifier: String, value: ItemService) = set(ItemIdentifierImpl(identifier), value)

    operator fun set(identifier: UUID, value: ItemService) = set(ItemIdentifierImpl(identifier), value)

}
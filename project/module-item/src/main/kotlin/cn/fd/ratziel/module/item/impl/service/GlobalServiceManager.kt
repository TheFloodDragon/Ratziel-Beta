package cn.fd.ratziel.module.item.impl.service

import cn.fd.ratziel.core.Identifier
import cn.fd.ratziel.core.TheIdentifier
import cn.fd.ratziel.module.item.api.service.ItemService
import cn.fd.ratziel.module.item.api.service.ItemServiceManager
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
    val groups: MutableMap<Identifier, ItemService> = ConcurrentHashMap()

    /**
     * 获取物品的服务
     */
    override fun get(identifier: Identifier): ItemService {
        return groups.computeIfAbsent(identifier) { DefaultItemService(it) }
    }

    /**
     * 设置物品的服务
     */
    override fun set(identifier: Identifier, value: ItemService) {
        groups[identifier] = value
    }

    operator fun get(identifier: String) = get(TheIdentifier(identifier))

    operator fun get(identifier: UUID) = get(TheIdentifier(identifier))

    operator fun set(identifier: String, value: ItemService) = set(TheIdentifier(identifier), value)

    operator fun set(identifier: UUID, value: ItemService) = set(TheIdentifier(identifier), value)

}
package cn.fd.ratziel.module.item.api.service

import cn.fd.ratziel.core.Identifier
import java.util.concurrent.ConcurrentHashMap

/**
 * ItemServiceManager
 *
 * @author TheFloodDragon
 * @since 2025/8/25 11:58
 */
object ItemServiceManager {

    /**
     * 物品服务组
     */
    @JvmField
    val holders: MutableMap<Identifier, ItemServiceHolder> = ConcurrentHashMap()

    /**
     * 获取物品的服务
     */
    @JvmStatic
    operator fun get(identifier: Identifier): ItemServiceHolder {
        return this.holders.computeIfAbsent(identifier) { ItemServiceHolder(it) }
    }

    /**
     * 设置物品的服务
     */
    @JvmStatic
    operator fun set(identifier: Identifier, value: ItemServiceHolder) {
        this.holders[identifier] = value
    }

}
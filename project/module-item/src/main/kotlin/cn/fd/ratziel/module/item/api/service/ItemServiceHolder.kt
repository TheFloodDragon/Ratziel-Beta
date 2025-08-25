package cn.fd.ratziel.module.item.api.service

import cn.fd.ratziel.core.Identifier
import cn.fd.ratziel.core.service.ServiceHolder

/**
 * ItemServiceHolder
 *
 * @author TheFloodDragon
 * @since 2025/8/25 11:28
 */
class ItemServiceHolder(val identifier: Identifier) : ServiceHolder {

    override fun <T> get(type: Class<T>): T? {
        return ItemServiceRegistry[type]?.get(identifier)
    }

    override fun <T> set(type: Class<T>, value: T) {
        ItemServiceRegistry[type]?.set(identifier, value)
    }

}
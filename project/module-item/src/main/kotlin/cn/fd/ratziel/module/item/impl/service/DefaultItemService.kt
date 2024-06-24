package cn.fd.ratziel.module.item.impl.service

import cn.fd.ratziel.core.Identifier
import cn.fd.ratziel.module.item.api.service.ItemService
import cn.fd.ratziel.module.item.api.service.ItemServiceRegistry

/**
 * DefaultItemService
 *
 * @author TheFloodDragon
 * @since 2024/5/4 10:52
 */
open class DefaultItemService(val identifier: Identifier) : ItemService {

    override fun <T> getServiceBy(type: Class<T>, registry: ItemServiceRegistry): T? {
        return registry.getter(type)?.apply(identifier)
    }

    override fun <T> setServiceBy(type: Class<T>, registry: ItemServiceRegistry, value: T) {
        registry.setter(type)?.accept(identifier, value)
    }

    /**
     * 默认使用 [GlobalServiceRegistry]
     */
    override fun <T> getService(type: Class<T>): T? = getServiceBy(type, GlobalServiceRegistry)

    override fun <T> setService(type: Class<T>, value: T) = setServiceBy(type, GlobalServiceRegistry, value)

}
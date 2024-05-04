package cn.fd.ratziel.module.item.impl.service

import cn.fd.ratziel.function.util.uncheck
import cn.fd.ratziel.module.item.api.service.ItemServiceFunction
import cn.fd.ratziel.module.item.api.service.ItemServiceGetter
import cn.fd.ratziel.module.item.api.service.ItemServiceRegistry
import cn.fd.ratziel.module.item.api.service.ItemServiceSetter

/**
 * BaseServiceRegistry
 *
 * @author TheFloodDragon
 * @since 2024/5/4 12:28
 */
abstract class BaseServiceRegistry : ItemServiceRegistry {

    protected abstract val registry: MutableMap<Class<*>, ItemServiceFunction<*>>

    override fun <T> getter(type: Class<T>): ItemServiceGetter<T>? = registry[type]?.getter?.let { uncheck(it) }

    override fun <T> setter(type: Class<T>): ItemServiceSetter<T>? = registry[type]?.setter?.let { uncheck(it) }

    override fun <T> register(type: Class<T>, function: ItemServiceFunction<T>) {
        registry[type] = function
    }

    override fun unregister(type: Class<*>) {
        registry.remove(type)
    }

}
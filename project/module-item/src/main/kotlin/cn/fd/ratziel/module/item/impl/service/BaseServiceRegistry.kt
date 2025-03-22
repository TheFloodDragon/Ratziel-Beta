package cn.fd.ratziel.module.item.impl.service

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

    override fun <T> getter(type: Class<T>): ItemServiceGetter<T>? {
        val getter = registry[type]?.getter
        @Suppress("UNCHECKED_CAST")
        return if (getter != null) getter as ItemServiceGetter<T> else null
    }

    override fun <T> setter(type: Class<T>): ItemServiceSetter<T>? {
        val setter = registry[type]?.setter
        @Suppress("UNCHECKED_CAST")
        return if (setter != null) setter as ItemServiceSetter<T> else null
    }

    override fun <T> register(type: Class<T>, function: ItemServiceFunction<T>) {
        registry[type] = function
    }

    override fun unregister(type: Class<*>) {
        registry.remove(type)
    }

}
package cn.fd.ratziel.module.item.feature.service

import cn.fd.ratziel.module.item.api.service.ItemServiceFunction
import cn.fd.ratziel.module.item.api.service.ItemServiceGetter
import cn.fd.ratziel.module.item.api.service.ItemServiceRegistry
import cn.fd.ratziel.module.item.api.service.ItemServiceSetter
import java.util.concurrent.CopyOnWriteArraySet

/**
 * GlobalServiceRegistry
 *
 * @author TheFloodDragon
 * @since 2024/5/4 11:08
 */
object GlobalServiceRegistry : ItemServiceRegistry {

    /**
     * 服务注册表的注册表 (有点绕)
     */
    val registries: MutableSet<ItemServiceRegistry> = CopyOnWriteArraySet()

    override fun <T> getter(type: Class<T>): ItemServiceGetter<T>? {
        for (registry in registries) {
            val getter = registry.getter(type)
            if (getter != null) return getter
        }
        return null
    }

    override fun <T> setter(type: Class<T>): ItemServiceSetter<T>? {
        for (registry in registries) {
            val setter = registry.setter(type)
            if (setter != null) return setter
        }
        return null
    }

    /**
     * 直接取消注册所有 [ItemServiceRegistry] 对应类型
     */
    override fun unregister(type: Class<*>) = registries.forEach { it.unregister(type) }

    /**
     * 不支持, 我总不能给所有的都注册一遍吧
     */
    override fun <T> register(type: Class<T>, function: ItemServiceFunction<T>) {
        throw UnsupportedOperationException("GlobalServiceRegistry don't allow to register any service in any way!")
    }

}
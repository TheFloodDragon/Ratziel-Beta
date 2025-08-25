package cn.fd.ratziel.core.service

import cn.fd.ratziel.core.Identifier
import java.util.concurrent.ConcurrentHashMap
import java.util.function.BiConsumer
import java.util.function.Function

/**
 * AbstractServiceRegistry
 *
 * @author TheFloodDragon
 * @since 2025/8/25 12:01
 */
abstract class AbstractServiceRegistry : ServiceRegistry {

    /**
     * 服务注册表
     */
    override val services: MutableMap<Class<*>, ServiceLinkage<*>> = ConcurrentHashMap()

    /**
     * 获取 [ServiceLinkage]
     */
    override fun <T> get(type: Class<T>): ServiceLinkage<T>? {
        @Suppress("UNCHECKED_CAST")
        return this.services[type] as? ServiceLinkage<T>
    }

    /**
     * 注册服务
     */
    override fun <T> register(type: Class<T>, linkage: ServiceLinkage<T>) {
        this.services[type] = linkage
    }

    /**
     * 注册服务
     */
    open fun <T> register(type: Class<T>, getter: Function<Identifier, T?>, setter: BiConsumer<Identifier, T>? = null) {
        val linkage = object : ServiceLinkage<T> {
            override val type = type
            override fun get(identifier: Identifier) = getter.apply(identifier)
            override fun set(identifier: Identifier, value: T) = setter?.accept(identifier, value) ?: super.set(identifier, value)
        }
        this.register(type, linkage)
    }

    /**
     * 取消注册服务
     */
    override fun unregister(type: Class<*>) {
        this.services.remove(type)
    }

}
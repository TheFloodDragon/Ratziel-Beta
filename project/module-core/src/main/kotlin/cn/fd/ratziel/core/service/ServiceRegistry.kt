package cn.fd.ratziel.core.service

/**
 * ServiceRegistry
 *
 * @author TheFloodDragon
 * @since 2025/8/25 11:20
 */
interface ServiceRegistry {

    /**
     * 注册的所有服务
     */
    val services: Map<Class<*>, ServiceLinkage<*>>

    /**
     * 获取 [ServiceLinkage]
     */
    operator fun <T> get(type: Class<T>): ServiceLinkage<T>?

    /**
     * 注册指定类型的服务
     */
    fun <T> register(type: Class<T>, linkage: ServiceLinkage<T>)

    /**
     * 取消注册指定类型的服务
     */
    fun unregister(type: Class<*>)

}
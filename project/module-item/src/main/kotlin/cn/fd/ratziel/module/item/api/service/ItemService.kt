package cn.fd.ratziel.module.item.api.service

/**
 * ItemService - 物品服务
 *
 * @author TheFloodDragon
 * @since 2024/5/2 21:52
 */
interface ItemService {

    /**
     * 获取服务
     */
    fun <T> getService(type: Class<T>): T?

    /**
     * 获取服务 (通过指定服务注册表)
     */
    fun <T> getServiceBy(type: Class<T>, registry: ItemServiceRegistry): T?

    /**
     * 设置服务
     */
    fun <T> setService(type: Class<T>, value: T)

    /**
     * 设置服务 (通过指定服务注册表)
     */
    fun <T> setServiceBy(type: Class<T>, registry: ItemServiceRegistry, value: T)

    /**
     * Kotlin 操作符优化
     * @see [getService]
     */
    operator fun <T> get(type: Class<T>) = getService(type)

    /**
     * Kotlin 操作符优化
     * @see [getServiceBy]
     */
    operator fun <T> get(type: Class<T>, registry: ItemServiceRegistry) = getServiceBy(type, registry)

    /**
     * Kotlin 操作符优化
     * @see [setService]
     */
    operator fun <T> set(type: Class<T>, value: T) = setService(type, value)

    /**
     * Kotlin 操作符优化
     * @see [setServiceBy]
     */
    operator fun <T> set(type: Class<T>, registry: ItemServiceRegistry, value: T) = setServiceBy(type, registry, value)

}
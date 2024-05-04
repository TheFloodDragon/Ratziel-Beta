package cn.fd.ratziel.module.item.api.service

/**
 * ItemServiceRegistry - 物品服务注册表
 *
 * @author TheFloodDragon
 * @since 2024/5/4 11:04
 */
interface ItemServiceRegistry {

    /**
     * 获取指定服务类型的 [getter]
     * @param type 指定类型
     */
    fun <T> getter(type: Class<T>): ItemServiceGetter<T>?

    /**
     * 获取指定服务类型的 [setter]
     * @param type 指定类型
     */
    fun <T> setter(type: Class<T>): ItemServiceSetter<T>?

    /**
     * 注册指定类型的服务
     * @param type 服务类型
     * @param function 服务 获取|设置 函数
     */
    fun <T> register(type: Class<T>, function: ItemServiceFunction<T>)

    /**
     * 注册指定类型的服务
     * @param type 服务类型
     * @param getter 服务获取函数
     * @param setter 服务设置函数
     */
    fun <T> register(type: Class<T>, getter: ItemServiceGetter<T>, setter: ItemServiceSetter<T>) = register(type, ItemServiceFunction(getter, setter))

    /**
     * 取消注册指定类型的服务
     * @param type 服务类型l
     */
    fun unregister(type: Class<*>)

}
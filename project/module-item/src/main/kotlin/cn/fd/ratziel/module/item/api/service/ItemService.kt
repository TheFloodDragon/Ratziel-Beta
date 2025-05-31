package cn.fd.ratziel.module.item.api.service

import cn.fd.ratziel.core.Identifier

/**
 * ItemService - 物品服务
 *
 * @author TheFloodDragon
 * @since 2024/5/2 21:52
 */
interface ItemService {

    /**
     * 物品唯一标识符
     */
    val identifier: Identifier

    /**
     * 获取
     */
    operator fun <T> get(type: Class<T>): T?

    /**
     * 设置
     */
    operator fun <T> set(type: Class<T>, value: T)

    /**
     * 获取 (通过指定服务注册表)
     */
    fun <T> get(type: Class<T>, registry: ItemServiceRegistry): T? {
        return registry.getter(type)?.apply(identifier)
    }

    /**
     * 设置 (通过指定服务注册表)
     */
    fun <T> set(type: Class<T>, registry: ItemServiceRegistry, value: T) {
        registry.setter(type)?.accept(identifier, value)
    }

}
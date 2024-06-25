package cn.fd.ratziel.module.item.api.registry

import cn.fd.ratziel.core.Priority
import cn.fd.ratziel.module.item.api.builder.ItemResolver

/**
 * ResolverRegistry - 物品解析器注册表
 *
 * @author TheFloodDragon
 * @since 2024/6/25 14:13
 */
interface ResolverRegistry {

    /**
     * 注册物品解析器
     * @param resolver 物品解析器
     * @param priority 优先级
     */
    fun register(resolver: ItemResolver, priority: Byte)

    /**
     * 注册物品解析器
     * @param resolver 物品解析器
     */
    fun register(resolver: ItemResolver) = register(resolver, Priority.DEFAULT_PRIORITY)

    /**
     * 取消注册指定类型的解析器
     * @param type 解析器类型
     */
    fun unregister(type: Class<out ItemResolver>)

    /**
     * 取消注册解析器
     * @param resolver 物品解析器
     */
    fun unregister(resolver: ItemResolver)

    /**
     * 获取指定类型的物品解析器
     */
    fun <T : ItemResolver> get(type: Class<T>): T? = getPriority(type)?.value

    /**
     * 获取指定类型的物品解析器 - [Priority]
     */
    fun <T : ItemResolver> getPriority(type: Class<T>): Priority<T>?

    /**
     * 判断指定类型的解析器是否被注册过
     * @param type 解析器类型
     */
    fun isRegistered(type: Class<out ItemResolver>): Boolean

    /**
     * 判断解析器是否被注册过
     * @param resolver 物品解析器
     */
    fun isRegistered(resolver: ItemResolver): Boolean

    /**
     * 获取所有注册的解析器
     */
    fun getResolvers(): Collection<ItemResolver>

    /**
     * 获取所有注册的解析器 - [Priority]
     */
    fun getResolversPriority(): Collection<Priority<ItemResolver>>

}
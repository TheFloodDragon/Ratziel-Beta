package cn.fd.ratziel.module.item.api.registry

import cn.fd.ratziel.core.Priority
import cn.fd.ratziel.module.item.api.builder.ItemTransformer

/**
 * ComponentRegistry - 物品组件注册表
 *
 * @author TheFloodDragon
 * @since 2024/6/25 13:32
 */
interface ComponentRegistry {

    /**
     * 注册组件以及其转换器
     * @param type 组件类型 (组件类)
     * @param transformer 绑定到组件上的物品转换器
     * @param priority 优先级
     */
    fun <T> register(type: Class<T>, transformer: ItemTransformer<out T>, priority: Byte)

    /**
     * 注册组件以及其转换器
     * @param type 组件类型 (组件类)
     * @param transformer 绑定到组件上的物品转换器
     */
    fun <T> register(type: Class<T>, transformer: ItemTransformer<out T>) = register(type, transformer, Priority.DEFAULT_PRIORITY)

    /**
     * 取消注册组件以及其转换器
     * @param type 组件类型 (组件类)
     */
    fun unregister(type: Class<*>)

    /**
     * 获取对应组件类型的转化器
     * @param type 组件类型 (组件类)
     * @return 对应组件类型的转化器, 当该组件未注册时, 返回空
     */
    fun <T> get(type: Class<T>): ItemTransformer<out T>? = getPriority(type)?.value

    /**
     * 获取对应组件类型的转化器 - [Priority]
     * @param type 组件类型 (组件类)
     * @return 对应组件类型的转化器, 当该组件未注册时, 返回空
     */
    fun <T> getPriority(type: Class<T>): Priority<ItemTransformer<out T>>?

    /**
     * 判断此组件是否被注册过
     * @param type 组件类型 (组件类)
     */
    fun isRegistered(type: Class<*>): Boolean

    /**
     * 获取注册表的 [Map] 形式
     */
    fun getRegistry(): Map<Class<*>, ItemTransformer<*>>

    /**
     * 获取注册表的 [Map] 形式 - [Priority]
     */
    fun getRegistryPriority(): Map<Class<*>, Priority<ItemTransformer<*>>>

}
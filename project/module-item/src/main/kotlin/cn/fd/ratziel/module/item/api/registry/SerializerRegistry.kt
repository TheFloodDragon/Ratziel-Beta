package cn.fd.ratziel.module.item.api.registry

import cn.fd.ratziel.module.item.api.builder.ItemSerializer

/**
 * SerializerRegistry - 物品序列化器注册表
 *
 * @author TheFloodDragon
 * @since 2024/6/25 13:34
 */
interface SerializerRegistry {

    /**
     * 注册序列化器
     * @param serializer 物品序列化器
     */
    fun register(serializer: ItemSerializer<*>)

    /**
     * 取消注册指定类型的序列化器
     * @param type 序列化器类型
     */
    fun unregister(type: Class<out ItemSerializer<*>>)

    /**
     * 取消注册序列化器
     * @param serializer 物品序列化器
     */
    fun unregister(serializer: ItemSerializer<*>)

    /**
     * 判断指定类型的序列化器是否被注册过
     * @param type 序列化器类型
     */
    fun isRegistered(type: Class<out ItemSerializer<*>>): Boolean

    /**
     * 判断序列化器是否被注册过
     * @param serializer 物品序列化器
     */
    fun isRegistered(serializer: ItemSerializer<*>): Boolean

    /**
     * 获取所有注册的序列化器
     */
    fun getSerializers(): Collection<ItemSerializer<*>>

}
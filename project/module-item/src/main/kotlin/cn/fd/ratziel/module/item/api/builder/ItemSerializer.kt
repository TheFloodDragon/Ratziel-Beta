package cn.fd.ratziel.module.item.api.builder

import cn.fd.ratziel.module.item.api.ItemComponent

/**
 * ItemSerializer - 物品序列化器
 *
 * @author TheFloodDragon
 * @since 2024/4/4 19:50
 */
interface ItemSerializer<E, T : ItemComponent<*, *>> {

    /**
     * 序列化 - 组件[T]到元素[E]
     */
    fun serialize(component: T): E

    /**
     * 反序列化 - 元素[E]到组件[T]
     */
    fun deserialize(element: E): T

    /**
     * 获取被该物品序列化器所使用(占用)的节点
     */
    fun getOccupiedNodes(): Array<String>

}
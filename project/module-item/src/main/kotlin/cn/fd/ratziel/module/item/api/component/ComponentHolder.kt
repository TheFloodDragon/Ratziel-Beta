package cn.fd.ratziel.module.item.api.component

/**
 * ComponentHolder
 * 
 * @author TheFloodDragon
 * @since 2025/12/30 22:43
 */
interface ComponentHolder {

    /**
     * 获取组件
     */
    operator fun <T : Any> get(type: ItemComponentType<T>): T?

    /**
     * 设置组件
     */
    operator fun <T : Any> set(type: ItemComponentType<T>, value: T)

    /**
     * 删除组件 (彻底删除)
     */
    fun remove(type: ItemComponentType<*>)

    /**
     * 恢复组件 (清空组件)
     */
    fun restore(type: ItemComponentType<*>)

}
package cn.fd.ratziel.module.item.api

/**
 * ComponentHolder
 *
 * @author TheFloodDragon
 * @since 2025/8/3 12:13
 */
interface ComponentHolder {

    /**
     * 获取组件
     *
     * @param type 组件类型
     */
    fun <T> getComponent(type: Class<T>): T

    /**
     * 设置组件
     *
     * @param component 组件
     */
    fun setComponent(component: Any)

}
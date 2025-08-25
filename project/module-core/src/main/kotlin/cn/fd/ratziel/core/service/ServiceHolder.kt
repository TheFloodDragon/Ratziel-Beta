package cn.fd.ratziel.core.service

import java.util.function.Supplier

/**
 * ServiceHolder
 *
 * @author TheFloodDragon
 * @since 2025/8/25 11:18
 */
interface ServiceHolder {

    /**
     * 获取指定类型的服务对象
     *
     * @param type 服务类型
     * @return 服务对象 (服务不存在时返回为空)
     */
    operator fun <T> get(type: Class<T>): T?

    /**
     * 设置指定类型的服务对象
     *
     * @param type 服务类型
     * @return 服务对象
     */
    @Throws(UnsupportedOperationException::class)
    operator fun <T> set(type: Class<T>, value: T)

    /**
     * 获取指定类型的服务对象
     *
     * @return 服务对象 (服务不存在时设置默认值)
     */
    fun <T> getOrPut(type: Class<T>, def: Supplier<T>): T {
        val obj = this[type]
        if (obj == null) {
            val default = def.get()
            this[type] = default
            return default
        } else return obj
    }

}
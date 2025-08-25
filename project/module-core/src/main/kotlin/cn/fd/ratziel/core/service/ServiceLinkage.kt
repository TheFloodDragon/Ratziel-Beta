package cn.fd.ratziel.core.service

import cn.fd.ratziel.core.Identifier

/**
 * ServiceLinkage
 *
 * @author TheFloodDragon
 * @since 2025/8/25 11:40
 */
interface ServiceLinkage<T> {

    /**
     * 服务类型
     */
    val type: Class<T>

    /**
     * 获取服务对象
     *
     * @return 服务对象 (服务不存在时返回为空)
     */
    fun get(identifier: Identifier): T?

    /**
     * 设置服务对象
     *
     * @return 服务对象
     */
    @Throws(UnsupportedOperationException::class)
    fun set(identifier: Identifier, value: T): Unit = throw UnsupportedOperationException("Service typed with $type does not support setting.")

}

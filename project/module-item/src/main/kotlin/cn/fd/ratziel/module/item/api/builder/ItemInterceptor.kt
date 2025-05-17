package cn.fd.ratziel.module.item.api.builder

import kotlinx.coroutines.CoroutineScope

/**
 * ItemInterceptor - 物品解释器
 *
 * @author TheFloodDragon
 * @since 2025/5/10 15:22
 */
interface ItemInterceptor {

    /**
     * 解释物品流
     *
     * @param scope 协程作用域
     * @param stream 物品流
     */
    suspend fun intercept(scope: CoroutineScope, stream: ItemStream)

}
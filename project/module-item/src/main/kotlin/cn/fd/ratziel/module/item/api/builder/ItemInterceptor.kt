package cn.fd.ratziel.module.item.api.builder

import cn.fd.ratziel.core.function.ArgumentContext

/**
 * ItemInterceptor
 *
 * @author TheFloodDragon
 * @since 2025/5/10 15:22
 */
interface ItemInterceptor {

    /**
     * 解释物品流
     *
     * @param stream 物品流
     * @param context 上下文
     */
    suspend fun intercept(stream: ItemStream, context: ArgumentContext)

}
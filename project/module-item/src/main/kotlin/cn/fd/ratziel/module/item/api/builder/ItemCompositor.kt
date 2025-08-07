package cn.fd.ratziel.module.item.api.builder

import kotlinx.coroutines.Deferred

/**
 * ItemCompositor
 *
 * @author TheFloodDragon
 * @since 2025/6/15 09:33
 */
interface ItemCompositor {

    /**
     * 调度物品解释器预处理基流。
     */
    fun prepare()

    /**
     * 调度物品解释器处理物品流. (编排任务并处理物品流)
     */
    suspend fun dispatch(stream: ItemStream)

    /**
     * 获取物品解释器.
     */
    fun <T : ItemInterpreter> getInterpreter(type: Class<T>): T

    /**
     * StreamCompositor
     *
     * @author TheFloodDragon
     * @since 2025/8/7 17:40
     */
    interface StreamCompositor : ItemCompositor {

        /**
         * 物品基流
         */
        val baseStream: ItemStream

        /**
         * 生产新的输出流
         */
        fun produce(): Deferred<ItemStream>

    }

}
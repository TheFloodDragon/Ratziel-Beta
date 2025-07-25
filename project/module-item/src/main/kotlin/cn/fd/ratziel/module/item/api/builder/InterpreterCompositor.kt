package cn.fd.ratziel.module.item.api.builder

/**
 * InterpreterCompositor
 *
 * @author TheFloodDragon
 * @since 2025/6/15 09:33
 */
interface InterpreterCompositor {

    /**
     * 调度物品解释器处理物品流. (编排任务并处理物品流)
     */
    suspend fun dispatch(stream: ItemStream)

    /**
     * 获取物品解释器.
     */
    fun <T : ItemInterpreter> getInterpreter(type: Class<T>): T

}
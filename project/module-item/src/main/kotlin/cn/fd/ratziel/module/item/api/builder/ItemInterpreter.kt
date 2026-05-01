package cn.fd.ratziel.module.item.api.builder

/**
 * ItemInterpreter - 物品解释器
 *
 * @author TheFloodDragon
 * @since 2025/5/10 15:22
 */
interface ItemInterpreter {

    /**
     * 预解释
     *
     * @return 该解释器是否应该被启用
     */
    suspend fun preFlow(stream: ItemStream): Boolean = true

    /**
     * 解释物品流
     *
     * @param stream 物品流
     */
    suspend fun interpret(stream: ItemStream) = Unit

}
package cn.fd.ratziel.module.item.api.builder

/**
 * ItemInterpreter - 物品解释器
 *
 * @author TheFloodDragon
 * @since 2025/5/10 15:22
 */
interface ItemInterpreter {

    /**
     * 解释物品流
     *
     * @param stream 物品流
     */
    suspend fun interpret(stream: ItemStream)

    /**
     * PreInterpretable - 标记 [ItemInterpreter] 为 可预解释型
     */
    annotation class PreInterpretable(val onlyPre: Boolean = true)

}
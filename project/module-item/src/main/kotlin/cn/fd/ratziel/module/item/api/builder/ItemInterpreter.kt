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
     * PreInterpretable - 可预解释型 [ItemInterpreter]
     */
    interface PreInterpretable : ItemInterpreter {

        /**
         * 预解释
         */
        suspend fun preFlow(stream: ItemStream)

        override suspend fun interpret(stream: ItemStream) = Unit

    }

    /**
     * AsyncInterpretation - 可异步解释标记
     */
    annotation class AsyncInterpretation

}
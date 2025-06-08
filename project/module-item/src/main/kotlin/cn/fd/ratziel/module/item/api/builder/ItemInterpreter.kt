package cn.fd.ratziel.module.item.api.builder

import cn.fd.ratziel.core.Identifier
import cn.fd.ratziel.core.element.Element

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
     * ElementInterpreter
     */
    interface ElementInterpreter : ItemInterpreter {

        /**
         * 解释物品元素
         *
         * @param identifier 物品标识符
         * @param element 物品元素
         */
        fun interpret(identifier: Identifier, element: Element)

        /**
         * 解释物品流
         */
        override suspend fun interpret(stream: ItemStream) = interpret(stream.identifier, stream.origin)

    }

}
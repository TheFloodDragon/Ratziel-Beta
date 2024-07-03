package cn.fd.ratziel.module.item.api.feature

import cn.fd.ratziel.function.argument.ArgumentContext
import java.util.concurrent.ConcurrentHashMap

/**
 * ItemAction - 物品动作
 *
 * @author TheFloodDragon
 * @since 2024/7/3 15:11
 */
interface ItemAction {

    /**
     * 参数上下文
     */
    val context: ArgumentContext

    /**
     * 处理物品动作
     */
    fun handle()

    /**
     * ActionType - 物品动作类型
     *
     * @author TheFloodDragon
     * @since 2024/7/3 15:06
     */
    interface ActionType {

        /**
         * 物品动作类型名称
         */
        val name: String

        /**
         * 动作类型别名
         */
        val alias: Array<String>

    }

    /**
     * ActionMap - 物品动作表
     *
     * @author TheFloodDragon
     * @since 2024/7/3 15:06
     */
    open class ActionMap(
        open val map: MutableMap<ActionType, ItemAction> = ConcurrentHashMap(),
    ) : MutableMap<ActionType, ItemAction> by map

}
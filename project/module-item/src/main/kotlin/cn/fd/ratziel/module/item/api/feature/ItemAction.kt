package cn.fd.ratziel.module.item.api.feature

import cn.fd.ratziel.function.argument.ArgumentContext

/**
 * ItemAction - 物品动作
 *
 * @author TheFloodDragon
 * @since 2024/7/3 15:11
 */
interface ItemAction {

    /**
     * 执行物品动作
     */
    fun execute(context: ArgumentContext)

}
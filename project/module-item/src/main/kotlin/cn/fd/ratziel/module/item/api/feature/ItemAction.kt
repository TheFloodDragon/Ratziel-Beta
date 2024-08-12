package cn.fd.ratziel.module.item.api.feature

import cn.fd.ratziel.script.api.ScriptEnvironment

/**
 * ItemAction - 物品动作
 *
 * @author TheFloodDragon
 * @since 2024/7/3 15:11
 */
@Deprecated("需要重构")
interface ItemAction {

    /**
     * 执行物品动作
     */
    fun execute(context: ScriptEnvironment)

}
package cn.fd.ratziel.module.item.api.trigger

import cn.fd.ratziel.function.ArgumentContext

/**
 * TriggerAction - 由触发器触发的动作
 *
 * @author TheFloodDragon
 * @since 2024/8/13 13:39
 */
interface TriggerAction {

    /**
     * 执行动作
     * @param trigger 触发此动作的触发器
     * @param context 动作参数
     */
    fun execute(trigger: TriggerType, context: ArgumentContext)

}
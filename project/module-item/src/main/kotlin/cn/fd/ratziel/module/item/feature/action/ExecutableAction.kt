package cn.fd.ratziel.module.item.feature.action

import cn.fd.ratziel.function.ArgumentContext
import cn.fd.ratziel.module.item.api.action.TriggerAction
import cn.fd.ratziel.module.item.api.action.TriggerType
import cn.fd.ratziel.script.block.ExecutableBlock

/**
 * ExecutableAction - 可执行的动作
 *
 * @author TheFloodDragon
 * @since 2024/10/3 15:45
 */
open class ExecutableAction(
    /**
     * 动作语句块
     */
    val block: ExecutableBlock,
) : TriggerAction {

    override fun execute(trigger: TriggerType, context: ArgumentContext) {
        block.execute(context)
    }

}
package cn.fd.ratziel.module.item.impl.action

import cn.fd.ratziel.function.ArgumentContext
import cn.fd.ratziel.module.item.api.trigger.TriggerAction
import cn.fd.ratziel.module.item.api.trigger.TriggerType
import cn.fd.ratziel.script.api.EvaluableScript
import cn.fd.ratziel.script.api.ScriptEnvironment
import cn.fd.ratziel.script.impl.SimpleScriptEnv

/**
 * ScriptedAction
 *
 * @author TheFloodDragon
 * @since 2024/7/3 15:32
 */
open class ScriptedAction(
    /**
     * 可执行脚本
     */
    val script: EvaluableScript,
) : TriggerAction {

    override fun execute(trigger: TriggerType, context: ArgumentContext) {
        script.evaluate(context.popOr(ScriptEnvironment::class.java, SimpleScriptEnv()))
    }

}
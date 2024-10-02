package cn.fd.ratziel.module.item.feature.action

import cn.fd.ratziel.function.ArgumentContext
import cn.fd.ratziel.module.item.api.action.TriggerAction
import cn.fd.ratziel.module.item.api.action.TriggerType
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
        script.evaluate(context.getOr(ScriptEnvironment::class.java, SimpleScriptEnv()))
    }

}
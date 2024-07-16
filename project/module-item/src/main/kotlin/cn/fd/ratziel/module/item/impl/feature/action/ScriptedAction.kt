package cn.fd.ratziel.module.item.impl.feature.action

import cn.fd.ratziel.module.item.api.feature.ItemAction
import cn.fd.ratziel.script.api.EvaluableScript
import cn.fd.ratziel.script.api.ScriptEnvironment

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
) : ItemAction {

    override fun execute(context: ScriptEnvironment) {
        script.evaluate(context)
    }

}
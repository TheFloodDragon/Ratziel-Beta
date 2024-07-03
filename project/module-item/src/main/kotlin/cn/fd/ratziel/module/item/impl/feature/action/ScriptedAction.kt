package cn.fd.ratziel.module.item.impl.feature.action

import cn.fd.ratziel.function.argument.ArgumentContext
import cn.fd.ratziel.module.item.api.feature.ItemAction
import cn.fd.ratziel.script.ScriptBlockBuilder
import cn.fd.ratziel.script.ScriptEnvironment
import cn.fd.ratziel.script.ScriptManager
import cn.fd.ratziel.script.SimpleScriptEnv

/**
 * ScriptedAction
 *
 * @author TheFloodDragon
 * @since 2024/7/3 15:32
 */
open class ScriptedAction(
    /**
     * 脚本动作块
     */
    val script: ScriptBlockBuilder.Block,
    /**
     * 脚本环境
     */
    val env: ScriptEnvironment = SimpleScriptEnv(),
) : ItemAction {

    override val context: ArgumentContext get() = env.argumentContext

    override fun handle() {
        script.evaluate(ScriptManager.defaultLang, env)
    }

}
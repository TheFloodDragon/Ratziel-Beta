package cn.fd.ratziel.module.item.impl.feature.action

import cn.fd.ratziel.function.argument.ArgumentContext
import cn.fd.ratziel.module.item.api.feature.ItemAction
import cn.fd.ratziel.script.ScriptBlockBuilder
import cn.fd.ratziel.script.ScriptManager
import cn.fd.ratziel.script.SimpleScriptEnv
import javax.script.Bindings
import javax.script.ScriptContext
import javax.script.SimpleBindings
import javax.script.SimpleScriptContext

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
) : ItemAction {

    override fun execute(context: ArgumentContext) {
        // 尝试获取绑定键
        var bindings: Bindings? = null
        for (arg in context.args()) {
            bindings = when (arg) {
                is Bindings -> arg
                is SimpleScriptEnv -> arg.bindings
                is ScriptContext -> arg.getBindings(SimpleScriptContext.ENGINE_SCOPE)
                else -> null
            }
            if (bindings != null) break
        }
        // 环境
        val env = SimpleScriptEnv(bindings ?: SimpleBindings(), context)
        // 执行
        script.evaluate(ScriptManager.defaultLang, env)
    }

}
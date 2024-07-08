package cn.fd.ratziel.module.item.impl.feature.action

import cn.fd.ratziel.function.argument.ArgumentContext
import cn.fd.ratziel.module.item.api.feature.ItemAction
import cn.fd.ratziel.script.ScriptBlockBuilder
import cn.fd.ratziel.script.ScriptManager
import cn.fd.ratziel.script.SimpleScriptEnv
import javax.script.Bindings
import javax.script.SimpleBindings

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
        // 获取绑定键
        val bindings: Bindings = context.popOr(Bindings::class.java, SimpleBindings())
        // 环境
        val env = SimpleScriptEnv(bindings, context)
        // 执行
        script.evaluate(ScriptManager.defaultLang, env)
    }

}
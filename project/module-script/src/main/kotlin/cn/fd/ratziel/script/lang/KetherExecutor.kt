package cn.fd.ratziel.script.lang

import cn.fd.ratziel.script.api.ScriptContent
import cn.fd.ratziel.script.api.ScriptEnvironment
import cn.fd.ratziel.script.api.ScriptExecutor
import cn.fd.ratziel.script.impl.SimpleScriptContent
import taboolib.module.kether.KetherShell
import taboolib.module.kether.ScriptOptions

/**
 * KetherExecutor
 *
 * @author TheFloodDragon
 * @since 2024/7/14 21:40
 */
object KetherExecutor : ScriptExecutor {

    override fun build(script: String) = SimpleScriptContent(script, this)

    override fun evaluate(script: ScriptContent, environment: ScriptEnvironment): Any? {
        return KetherShell.eval(script.content, ScriptOptions.new { vars(environment.bindings) })
    }

}
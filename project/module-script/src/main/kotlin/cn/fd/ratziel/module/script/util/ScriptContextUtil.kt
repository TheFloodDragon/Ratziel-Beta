package cn.fd.ratziel.module.script.util

import cn.fd.ratziel.common.util.VariablesMap
import cn.fd.ratziel.common.util.varsMap
import cn.fd.ratziel.core.contextual.ArgumentContext
import cn.fd.ratziel.module.script.api.LiteralScriptContent
import cn.fd.ratziel.module.script.api.ScriptEnvironment
import cn.fd.ratziel.module.script.api.ScriptExecutor

fun ArgumentContext.scriptEnv(): ScriptEnvironment {
    return this.popOrPut(ScriptEnvironment::class.java) {
        ScriptEnvironment(this.varsMap())
    }
}

fun ScriptExecutor.eval(content: String, vars: VariablesMap): Any? {
    return this.evaluate(LiteralScriptContent(content, this), ScriptEnvironment(vars))
}

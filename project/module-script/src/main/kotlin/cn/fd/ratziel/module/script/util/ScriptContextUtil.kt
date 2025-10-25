package cn.fd.ratziel.module.script.util

import cn.fd.ratziel.common.util.VariablesMap
import cn.fd.ratziel.common.util.varsMap
import cn.fd.ratziel.core.contextual.ArgumentContext
import cn.fd.ratziel.module.script.api.ScriptContent
import cn.fd.ratziel.module.script.api.ScriptEnvironment
import cn.fd.ratziel.module.script.api.ScriptExecutor
import cn.fd.ratziel.module.script.api.ScriptSource

fun ArgumentContext.scriptEnv(): ScriptEnvironment {
    return this.popOrPut(ScriptEnvironment::class.java) {
        ScriptEnvironment(this.varsMap())
    }
}

fun ScriptExecutor.eval(content: ScriptContent, vars: VariablesMap): Any? {
    return this.evaluate(content, ScriptEnvironment(vars))
}

fun ScriptExecutor.eval(content: String, vars: VariablesMap): Any? {
    return this.eval(ScriptContent.literal(content), vars)
}

fun ScriptExecutor.eval(content: String, vars: VariablesMap.() -> Unit): Any? {
    return this.eval(content, VariablesMap().apply(vars))
}

fun ScriptExecutor.compile(content: String, vars: VariablesMap): ScriptContent {
    return this.build(ScriptSource.literal(content), ScriptEnvironment(vars))
}

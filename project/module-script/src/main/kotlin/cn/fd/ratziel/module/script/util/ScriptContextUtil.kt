package cn.fd.ratziel.module.script.util

import cn.fd.ratziel.common.util.VariablesMap
import cn.fd.ratziel.common.util.varsMap
import cn.fd.ratziel.core.contextual.ArgumentContext
import cn.fd.ratziel.module.script.api.ScriptContent
import cn.fd.ratziel.module.script.api.ScriptEnvironment
import cn.fd.ratziel.module.script.api.ScriptSource
import cn.fd.ratziel.module.script.api.ScriptType

fun ArgumentContext.scriptEnv(): ScriptEnvironment {
    return this.popOrPut(ScriptEnvironment::class.java) {
        ScriptEnvironment(this.varsMap())
    }
}

fun ScriptType.eval(content: ScriptContent, vars: VariablesMap): Any? {
    return this.executor.eval(content, ScriptEnvironment(vars))
}

fun ScriptType.eval(content: String, vars: VariablesMap): Any? {
    return this.eval(ScriptContent.literal(content, this), vars)
}

fun ScriptType.eval(content: String, vars: VariablesMap.() -> Unit): Any? {
    return this.eval(content, VariablesMap().apply(vars))
}

fun ScriptType.compile(content: String, vars: VariablesMap): ScriptContent? {
    return this.executor.build(ScriptSource.literal(content, this), ScriptEnvironment(vars)).getOrNull()
}

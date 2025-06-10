package cn.fd.ratziel.module.script.util

import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.module.script.api.ScriptEnvironment
import cn.fd.ratziel.module.script.impl.SimpleScriptEnvironment
import cn.fd.ratziel.module.script.impl.VariablesMap

fun ArgumentContext.scriptEnv(): ScriptEnvironment {
    val environment = this.popOr(ScriptEnvironment::class.java) { SimpleScriptEnvironment() }
    environment.set("argumentContext", this)
    return environment
}

fun ArgumentContext.varsMap(): VariablesMap =
    popOr(VariablesMap::class.java) {
        VariablesMap().also { put(it) }
    }

package cn.fd.ratziel.module.script.util

import cn.fd.ratziel.core.functional.ArgumentContext
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
        val vars = VariablesMap()
        // 加入到上下文中
        this.put(vars)
        // 接受上下文, 将上下文中的参数转化为变量导入 vars
        vars.accept(this)
        return@popOr vars
    }

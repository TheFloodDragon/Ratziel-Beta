package cn.fd.ratziel.module.script.util

import cn.fd.ratziel.core.contextual.ArgumentContext
import cn.fd.ratziel.module.script.api.ScriptEnvironment
import cn.fd.ratziel.module.script.api.ScriptExecutor
import cn.fd.ratziel.module.script.impl.ScriptEnvironmentImpl

fun ArgumentContext.scriptEnv(): ScriptEnvironment {
    return this.popOrPut(ScriptEnvironment::class.java) {
        ScriptEnvironmentImpl(this.varsMap())
    }
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

fun ScriptExecutor.eval(content: String, vars: VariablesMap): Any? {
    return this.evaluate(content, ScriptEnvironmentImpl(vars))
}

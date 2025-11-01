@file:Suppress("NOTHING_TO_INLINE")

package cn.fd.ratziel.module.script.util

import cn.fd.ratziel.common.util.VariablesMap
import cn.fd.ratziel.common.util.varsMap
import cn.fd.ratziel.core.contextual.ArgumentContext
import cn.fd.ratziel.module.script.api.CompiledScript
import cn.fd.ratziel.module.script.api.ScriptContent
import cn.fd.ratziel.module.script.api.ScriptEnvironment

fun ArgumentContext.scriptEnv(): ScriptEnvironment {
    return this.popOrPut(ScriptEnvironment::class.java) {
        ScriptEnvironment(this.varsMap())
    }
}

inline fun VariablesMap.toScriptEnv() = ScriptEnvironment(this)

/**
 * 快速评估脚本 (带编译后的脚本检测)
 */
fun ScriptContent.eval(environment: ScriptEnvironment): Any? {
    return if (this is CompiledScript) {
        this.eval(environment)
    } else {
        this.source.language.executor.eval(this, environment)
    }
}

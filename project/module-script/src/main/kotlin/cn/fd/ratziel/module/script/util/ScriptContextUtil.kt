package cn.fd.ratziel.module.script.util

import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.module.script.api.ScriptEnvironment
import cn.fd.ratziel.module.script.impl.SimpleScriptEnvironment

fun ArgumentContext.scriptEnv() = this.popOr(ScriptEnvironment::class.java) { SimpleScriptEnvironment() }

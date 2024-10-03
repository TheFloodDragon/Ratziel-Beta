package cn.fd.ratziel.script.util

import cn.fd.ratziel.function.ArgumentContext
import cn.fd.ratziel.script.api.ScriptEnvironment

fun ArgumentContext.scriptEnv() = this.popOrNull(ScriptEnvironment::class.java)
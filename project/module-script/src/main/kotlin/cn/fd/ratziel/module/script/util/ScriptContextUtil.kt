package cn.fd.ratziel.module.script.util

import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.module.script.api.ScriptEnvironment

fun ArgumentContext.scriptEnv() = this.popOrNull(ScriptEnvironment::class.java)
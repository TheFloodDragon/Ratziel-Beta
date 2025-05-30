package cn.fd.ratziel.module.script.impl

import cn.fd.ratziel.module.script.ScriptManager
import cn.fd.ratziel.module.script.api.ScriptEnvironment
import javax.script.ScriptContext
import javax.script.SimpleScriptContext

/**
 * SimpleScriptEnv
 *
 * @author TheFloodDragon
 * @since 2025/5/4 15:19
 */
class SimpleScriptEnv(
    private val scriptContext: ScriptContext,
) : ScriptEnvironment {

    constructor() : this(SimpleScriptContext().apply {
        setBindings(ScriptManager.Global.globalBindings, ScriptContext.GLOBAL_SCOPE)
    })

    override fun getContext() = this.scriptContext

}
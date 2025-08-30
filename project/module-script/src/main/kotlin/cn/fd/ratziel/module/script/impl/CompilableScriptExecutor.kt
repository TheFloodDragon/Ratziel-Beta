package cn.fd.ratziel.module.script.impl

import cn.fd.ratziel.module.script.api.LiteralScriptContent
import cn.fd.ratziel.module.script.api.ScriptContent
import cn.fd.ratziel.module.script.api.ScriptEnvironment
import cn.fd.ratziel.module.script.api.ScriptExecutor
import taboolib.common.platform.function.warning

/**
 * CompilableScriptExecutor
 *
 * @author TheFloodDragon
 * @since 2025/4/26 10:49
 */
abstract class CompilableScriptExecutor<T : Any> : ScriptExecutor {

    /**
     * 直接评估脚本
     */
    abstract fun evalDirectly(script: String, environment: ScriptEnvironment): Any?

    /**
     * 编译脚本
     */
    abstract fun compile(script: String): T

    /**
     * 评估编译后的脚本
     */
    abstract fun evalCompiled(script: T, environment: ScriptEnvironment): Any?

    /**
     * 执行脚本
     */
    override fun evaluate(script: ScriptContent, environment: ScriptEnvironment): Any? {
        if (script is CompiledScript<*>) {
            @Suppress("UNCHECKED_CAST")
            val compiled = script.compiled as? T
            if (compiled != null) return evalCompiled(compiled, environment)
        }
        return evalDirectly(script.content, environment)
    }

    override fun evaluate(script: String, environment: ScriptEnvironment): Any? {
        return evalDirectly(script, environment)
    }

    /**
     * 构建脚本
     * @param compile 是否启用编译
     */
    fun build(script: String, compile: Boolean): ScriptContent {
        if (compile) {
            try {
                val compiled = this.compile(script)
                return CompiledScript(script, this, compiled)
            } catch (e: Exception) {
                warning("Cannot compile script by '$this' ! Script content: $script")
                e.printStackTrace()
            }
        }
        return LiteralScriptContent(script, this)
    }

    override fun build(script: String) = build(script, compile = true)

}

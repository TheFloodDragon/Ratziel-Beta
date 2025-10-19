package cn.fd.ratziel.module.script.impl

import cn.fd.ratziel.module.script.api.*
import taboolib.common.platform.function.warning

/**
 * CompilableScriptExecutor
 *
 * @author TheFloodDragon
 * @since 2025/4/26 10:49
 */
interface CompilableScriptExecutor<T : Any> : ScriptExecutor {

    /**
     * 编译脚本
     */
    fun compile(source: ScriptSource, environment: ScriptEnvironment): T

    /**
     * 直接评估脚本 (解释运行)
     */
    fun evalDirectly(source: ScriptSource, environment: ScriptEnvironment): Any?

    /**
     * 评估编译后的脚本 (编译运行)
     */
    fun evalCompiled(compiled: T, environment: ScriptEnvironment): Any?

    /**
     * 执行脚本
     */
    override fun evaluate(script: ScriptContent, environment: ScriptEnvironment): Any? {
        return if (script is CompiledScript<*> && script.executor == this) {
            @Suppress("UNCHECKED_CAST")
            val compiled = script.compiled as T
            evalCompiled(compiled, environment) // 编译运行
        } else evalDirectly(ScriptSource.literal(script.content), environment)
    }

    /**
     * 构建脚本
     * @param compile 是否启用编译
     */
    fun build(source: ScriptSource, environment: ScriptEnvironment, compile: Boolean): ScriptContent {
        if (compile) {
            try {
                val compiled = this.compile(source, environment)
                return CompiledScriptImpl(source.content, this, compiled)
            } catch (e: Exception) {
                warning("Failed to compile script by '$this' ! Script Source: $source")
                e.printStackTrace()
            }
        }
        return ScriptContent.literal(source.content)
    }

    override fun build(source: ScriptSource, environment: ScriptEnvironment) = this.build(source, environment, true)

}
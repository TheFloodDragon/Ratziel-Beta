package cn.fd.ratziel.module.script.impl

import cn.fd.ratziel.module.script.api.*

/**
 * IntegratedScriptExecutor
 *
 * @author TheFloodDragon
 * @since 2025/11/1 20:18
 */
abstract class IntegratedScriptExecutor : ScriptExecutor, ScriptCompiler, ScriptEvaluator {

    final override fun build(source: ScriptSource, environment: ScriptEnvironment): Result<ScriptContent> {
        return runCatching { this.compile(source, environment) }
    }

    final override fun eval(script: ScriptContent, environment: ScriptEnvironment): Result<Any?> {
        return if (script is CompiledScript) {
            runCatching { eval(script, environment) }
        } else {
            runCatching { this.evaluate(script, environment) }
        }
    }

}
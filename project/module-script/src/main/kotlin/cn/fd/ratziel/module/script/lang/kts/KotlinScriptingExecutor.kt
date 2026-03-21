package cn.fd.ratziel.module.script.lang.kts

import cn.fd.ratziel.core.contextual.AttachedContext
import cn.fd.ratziel.module.script.api.*
import cn.fd.ratziel.module.script.conf.ScriptConfigurationKeys
import cn.fd.ratziel.module.script.conf.scriptImporting
import javax.script.*
import javax.script.CompiledScript
import kotlin.script.experimental.api.defaultImports

/**
 * KotlinScriptingExecutor
 *
 * @author TheFloodDragon
 * @since 2025/10/3 14:17
 */
object KotlinScriptingExecutor : IntegratedScriptExecutor() {

    // 引擎捕获器（同名 key 被移除，这里使用固定的 Catcher 实例作为键）
    @JvmStatic
    private val engineCatcher by AttachedContext.catcher<ScriptEngine> {
        // 提供一个最小的兜底构造，实际构造在 fetch 的 ifAbsent 中完成
        KtsScriptEngineFactory.getScriptEngine()
    }

    override fun evaluate(script: ScriptContent, environment: ScriptEnvironment): Any? {
        return getEngine(environment).eval(script.content)
    }

    override fun compile(source: ScriptSource, environment: ScriptEnvironment): ValuedCompiledScript<CompiledScript> {
        val script = (getEngine(environment) as Compilable).compile(source.content)
        return object : ValuedCompiledScript<CompiledScript>(script, source) {
            override fun eval(environment: ScriptEnvironment): Any? {
                return script.eval(getEngine(environment).context)
            }
        }
    }

    /**
     * 从环境中获取脚本引擎 [ScriptEngine]
     */
    fun getEngine(environment: ScriptEnvironment): ScriptEngine {
        // 获取脚本引擎：按环境缓存
        val engine = environment.runningState.fetch(engineCatcher) {
            KtsScriptEngineFactory.getScriptEngine(compilationBody = {
                // 导入类包
                val imports = environment.configuration[ScriptConfigurationKeys.scriptImporting]
                // 更新默认导入
                defaultImports.update { origin ->
                    (origin ?: arrayListOf()) +
                            imports.packages.map { it.packageNameWithAsterisk } +
                            imports.classes.map { it.fullName }
                }
            })
        }
        // 设置环境的绑定键
        engine.setBindings(SimpleBindings(environment.bindings), ScriptContext.ENGINE_SCOPE)
        return engine
    }

    override fun compiler() = this
    override fun evaluator() = this

}
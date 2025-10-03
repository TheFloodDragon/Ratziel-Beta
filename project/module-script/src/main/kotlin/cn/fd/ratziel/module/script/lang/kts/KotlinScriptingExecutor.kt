package cn.fd.ratziel.module.script.lang.kts

import cn.fd.ratziel.module.script.api.ScriptEnvironment
import cn.fd.ratziel.module.script.api.ScriptSource
import cn.fd.ratziel.module.script.impl.CompilableScriptExecutor
import cn.fd.ratziel.module.script.imports.GroupImports
import javax.script.Compilable
import javax.script.CompiledScript
import javax.script.ScriptContext
import javax.script.ScriptEngine
import kotlin.script.experimental.api.defaultImports

/**
 * KotlinScriptingExecutor
 *
 * @author TheFloodDragon
 * @since 2025/10/3 14:17
 */
object KotlinScriptingExecutor : CompilableScriptExecutor<CompiledScript> {

    override fun evalDirectly(source: ScriptSource, environment: ScriptEnvironment): Any? {
        return getEngine(environment).eval(source.content)
    }

    override fun compile(source: ScriptSource, environment: ScriptEnvironment): CompiledScript {
        return (getEngine(environment) as Compilable).compile(source.content)
    }

    override fun evalCompiled(compiled: CompiledScript, environment: ScriptEnvironment): Any? {
        return compiled.eval(getEngine(environment).context)
    }

    /**
     * 从环境中获取脚本引擎 [ScriptEngine]
     */
    fun getEngine(environment: ScriptEnvironment): ScriptEngine {
        // 获取脚本引擎
        val engine = environment.context.fetch(this) {
            KtsScriptEngineFactory.getScriptEngine(compilationBody = {
                // 导入类包
                val imports = GroupImports.catcher[environment.context]
                // 更新默认导入
                defaultImports.update { origin ->
                    (origin ?: arrayListOf()) +
                            imports.packages.map { it.packageNameWithAsterisk } +
                            imports.classes.map { it.fullName }
                }
            })

        }
        // 设置环境的绑定键
        engine.setBindings(environment.bindings, ScriptContext.ENGINE_SCOPE)
        return engine
    }

}
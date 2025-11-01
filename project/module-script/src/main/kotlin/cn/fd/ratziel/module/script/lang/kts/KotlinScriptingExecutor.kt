package cn.fd.ratziel.module.script.lang.kts

import cn.fd.ratziel.module.script.api.ScriptContent
import cn.fd.ratziel.module.script.api.ScriptEnvironment
import cn.fd.ratziel.module.script.api.ScriptSource
import cn.fd.ratziel.module.script.api.ValuedCompiledScript
import cn.fd.ratziel.module.script.impl.IntegratedScriptExecutor
import cn.fd.ratziel.module.script.imports.GroupImports
import javax.script.*
import kotlin.script.experimental.api.defaultImports

/**
 * KotlinScriptingExecutor
 *
 * @author TheFloodDragon
 * @since 2025/10/3 14:17
 */
object KotlinScriptingExecutor : IntegratedScriptExecutor() {

    override fun evaluate(script: ScriptContent, environment: ScriptEnvironment): Any? {
        return getEngine(environment).eval(script.content)
    }

    override fun compile(source: ScriptSource, environment: ScriptEnvironment): ValuedCompiledScript<CompiledScript> {
        val script = (getEngine(environment) as Compilable).compile(source.content)
        return object : ValuedCompiledScript<CompiledScript>(script, source, this) {
            override fun eval(environment: ScriptEnvironment): Any? {
                return script.eval(getEngine(environment).context)
            }
        }
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
        engine.setBindings(SimpleBindings(environment.bindings), ScriptContext.ENGINE_SCOPE)
        return engine
    }

    override fun compiler() = this
    override fun evaluator() = this

}
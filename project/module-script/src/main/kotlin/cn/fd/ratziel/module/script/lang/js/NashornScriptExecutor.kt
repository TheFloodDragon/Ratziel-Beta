package cn.fd.ratziel.module.script.lang.js

import cn.fd.ratziel.module.script.ScriptManager
import cn.fd.ratziel.module.script.api.IntegratedScriptExecutor
import cn.fd.ratziel.module.script.api.ScriptContent
import cn.fd.ratziel.module.script.api.ScriptEnvironment
import cn.fd.ratziel.module.script.api.ScriptSource
import cn.fd.ratziel.module.script.impl.ImportedScriptContext
import cn.fd.ratziel.module.script.impl.ReplenishingScript
import cn.fd.ratziel.module.script.imports.GroupImports
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory
import javax.script.*

/**
 * NashornScriptExecutor
 *
 * @author TheFloodDragon
 * @since 2025/4/26 09:33
 */
class NashornScriptExecutor : IntegratedScriptExecutor() {

    /**
     * 编译原始脚本
     */
    override fun compile(source: ScriptSource, environment: ScriptEnvironment): CompiledNashornScript {
        val compiler = createRuntime(environment) as Compilable
        val script = compiler.compile(source.content)
        return CompiledNashornScript(script, environment, source)
    }

    /**
     * 直接运行脚本
     */
    override fun evaluate(script: ScriptContent, environment: ScriptEnvironment): Any? {
        return createRuntime(environment).eval(script.source.content)
    }

    /**
     * 创建运行时所需的上下文
     */
    fun createRuntime(environment: ScriptEnvironment): ScriptEngine {
        // 初始化预热脚本引擎
        val engine = newEngine().importBindings(environment)

        // 导入导入组
        val imports = GroupImports.catcher[environment.context]

        // 导入类、包
        if (imports.classes.isNotEmpty() || imports.packages.isNotEmpty()) {
            // 设置脚本引擎的脚本上下文 (用于导入类、包)
            engine.context = object : ImportedScriptContext(engine.context) {
                override fun getImport(name: String): Any? {
                    val clazz = super.getImport(name)
                    // 转化成 StaticClass 便于 Nashorn 使用
                    return clazz?.let { jdk.dynalink.beans.StaticClass.forClass(it as Class<*>) }
                }
            }.also { importedContext ->
                // 设置脚本上下文里的导入组
                importedContext.imports = imports
            }
        }

        // 导入导入组里的脚本
        val scriptImports = imports.scripts(JavaScriptLang)
        if (scriptImports.isNotEmpty()) {
            for (import in scriptImports) {
                this.evaluate(import.compiled ?: continue, environment)
            }
        }

        return engine
    }

    override fun compiler() = NashornScriptExecutor()
    override fun evaluator() = NashornScriptExecutor()

    inner class CompiledNashornScript(
        script: CompiledScript,
        compilationEnv: ScriptEnvironment,
        source: ScriptSource,
    ) : ReplenishingScript<CompiledScript, ScriptEngine>(script, compilationEnv, source, this) {
        override fun preheat() = createRuntime(compilationEnv)
        override fun eval(engine: ScriptEngine): Any? = script.eval(engine.context)
        override fun initRuntime(engine: ScriptEngine, runtimeEnv: ScriptEnvironment) {
            // 导入运行时的环境绑定键
            engine.importBindings(runtimeEnv)
        }
    }


    companion object {

        /**
         * 默认脚本实例
         */
        @JvmField
        val DEFAULT = NashornScriptExecutor()

        /**
         * 导入环境的绑定键
         */
        @JvmStatic
        fun ScriptEngine.importBindings(environment: ScriptEnvironment): ScriptEngine = this.also { engine ->
            // 设置环境的绑定键 (直接导入全局域)
            engine.setBindings(SimpleBindings(environment.bindings), ScriptContext.GLOBAL_SCOPE)
        }

        /**
         * 创建脚本引擎实例
         */
        @JvmStatic
        fun newEngine(): ScriptEngine {
            // 创建脚本引擎
            return scriptEngineFactory?.getScriptEngine(
                arrayOf("-Dnashorn.args=--language=es6"), this::class.java.classLoader
            ) ?: throw NullPointerException("Cannot find ScriptEngine for JavaScript(Nashorn) Language")
        }

        val scriptEngineFactory by lazy {
            ScriptManager.engineManager.engineFactories.find {
                it.engineName == "OpenJDK Nashorn"
            } as? NashornScriptEngineFactory
        }

    }

}
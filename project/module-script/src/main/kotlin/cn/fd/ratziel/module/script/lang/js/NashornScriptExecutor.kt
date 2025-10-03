package cn.fd.ratziel.module.script.lang.js

import cn.fd.ratziel.module.script.ScriptManager
import cn.fd.ratziel.module.script.api.ScriptEnvironment
import cn.fd.ratziel.module.script.api.ScriptSource
import cn.fd.ratziel.module.script.impl.CompilableScriptExecutor
import cn.fd.ratziel.module.script.impl.ImportedScriptContext
import cn.fd.ratziel.module.script.impl.CompileDefault
import cn.fd.ratziel.module.script.imports.GroupImports
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory
import javax.script.Compilable
import javax.script.CompiledScript
import javax.script.ScriptContext
import javax.script.ScriptEngine
import kotlin.concurrent.getOrSet

/**
 * NashornScriptExecutor
 *
 * @author TheFloodDragon
 * @since 2025/4/26 09:33
 */
object NashornScriptExecutor : CompilableScriptExecutor<NashornScriptExecutor.CachedCompiledScript>, CompileDefault {

    /**
     * 直接运行脚本
     */
    override fun evalDirectly(source: ScriptSource, environment: ScriptEnvironment): Any? {
        return getEngine(environment).eval(source.content)
    }

    /**
     * 编译原始脚本
     */
    override fun compile(source: ScriptSource, environment: ScriptEnvironment): CachedCompiledScript {
        val compiler = (getEngine(environment) as Compilable)
        return CachedCompiledScript(compiler.compile(source.content))
    }

    /**
     * 运行编译后的脚本
     */
    override fun evalCompiled(compiled: CachedCompiledScript, environment: ScriptEnvironment): Any? {
        // 获取当前线程的运行脚本引擎
        val runtime = compiled.local.getOrSet { getEngine(environment) }
        return compiled.script.eval(runtime.context)
    }

    /**
     * 从环境中获取脚本引擎 [ScriptEngine]
     */
    fun getEngine(environment: ScriptEnvironment): ScriptEngine {
        // 创建一个新的基础的 ScriptEngine
        val engine = newEngine()

        // 设置环境的绑定键 (直接导入全局域)
        engine.setBindings(environment.bindings, ScriptContext.GLOBAL_SCOPE)

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

    /**
     * 缓存的编译后的 [CompiledScript]
     */
    class CachedCompiledScript(val script: CompiledScript) {

        /**
         * [ThreadLocal] 存储每个线程的 [ScriptEngine]
         * 同一个线程共享一个 [ScriptEngine]
         */
        val local = ThreadLocal<ScriptEngine>()

    }

}
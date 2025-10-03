package cn.fd.ratziel.module.script.lang.js

import cn.fd.ratziel.module.script.ScriptManager
import cn.fd.ratziel.module.script.api.ScriptEnvironment
import cn.fd.ratziel.module.script.api.ScriptSource
import cn.fd.ratziel.module.script.impl.CompileDefault
import cn.fd.ratziel.module.script.impl.EnginedScriptExecutor
import cn.fd.ratziel.module.script.impl.ImportedScriptContext
import cn.fd.ratziel.module.script.imports.GroupImports
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory
import java.util.function.Supplier
import javax.script.Compilable
import javax.script.CompiledScript
import javax.script.ScriptContext
import javax.script.ScriptEngine

/**
 * NashornScriptExecutor
 *
 * @author TheFloodDragon
 * @since 2025/4/26 09:33
 */
object NashornScriptExecutor : EnginedScriptExecutor<CompiledScript, ScriptEngine>(), CompileDefault {

    /**
     * 直接运行脚本
     */
    override fun evalDirectly(source: ScriptSource, environment: ScriptEnvironment): Any? {
        return preheat(environment).eval(source.content)
    }

    /**
     * 编译原始脚本
     */
    override fun compile(source: ScriptSource, environment: ScriptEnvironment): CachedScript<CompiledScript, ScriptEngine> {
        val compiler = (getEngine(environment) { newEngine() } as Compilable)
        val script = compiler.compile(source.content)
        return CachedScript(script, environment, this)
    }

    /**
     * 运行编译后的脚本
     */
    override fun evalCompiled(compiled: CachedScript<CompiledScript, ScriptEngine>, environment: ScriptEnvironment): Any? {
        // 获取当前线程的运行脚本引擎
        val runtime = getEngine(environment) { compiled.get() }
        return compiled.script.eval(runtime.context)
    }

    override fun preheat(environment: ScriptEnvironment): ScriptEngine {
        // 初始化预热脚本引擎
        val engine = getEngine(environment) { newEngine() }

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
     * 从环境中获取脚本引擎 [ScriptEngine]
     */
    fun getEngine(environment: ScriptEnvironment, init: Supplier<ScriptEngine>): ScriptEngine {
        // 获取环境中的上下文 (创建)
        val engine = environment.context.fetch(this, init)
        // 设置环境的绑定键 (直接导入全局域)
        engine.setBindings(environment.bindings, ScriptContext.GLOBAL_SCOPE)
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

}
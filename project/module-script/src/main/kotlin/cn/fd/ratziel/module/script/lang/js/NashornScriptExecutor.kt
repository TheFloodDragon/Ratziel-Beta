package cn.fd.ratziel.module.script.lang.js

import cn.fd.ratziel.module.script.ScriptManager
import cn.fd.ratziel.module.script.ScriptType
import cn.fd.ratziel.module.script.api.ScriptEnvironment
import cn.fd.ratziel.module.script.impl.EnginedScriptExecutor
import cn.fd.ratziel.module.script.impl.ImportedScriptContext
import cn.fd.ratziel.module.script.imports.GroupImports
import cn.fd.ratziel.module.script.internal.NonStrictCompilation
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory
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
object NashornScriptExecutor : EnginedScriptExecutor<CompiledScript>(), NonStrictCompilation {

    /**
     * 直接运行脚本
     */
    override fun evalDirectly(script: String, environment: ScriptEnvironment): Any? {
        return getEngine(environment).eval(script)
    }

    /**
     * 编译原始脚本
     *
     * @param script 原始脚本
     */
    override fun compile(script: String, environment: ScriptEnvironment): CompiledScript {
        return (newEngine() as Compilable).compile(script)
    }

    /**
     * 运行编译后的脚本
     */
    override fun evalCompiled(script: CompiledScript, environment: ScriptEnvironment): Any? {
        return script.eval(getEngine(environment).context)
    }

    override fun preheat(environment: ScriptEnvironment) {
        val imports = GroupImports.catcher[environment.context]
        if (imports.isEmpty()) return
        // 获取脚本引擎上下文
        val context = getEngine(environment).context
        // 设置脚本上下文里的导入组
        (context as ImportedScriptContext).imports = imports
        // 导入导入组里的脚本
        val scriptImports = imports.scripts[ScriptType.JAVASCRIPT].orEmpty()
        for (import in scriptImports) {
            this.evaluate(import.compiled ?: continue, environment)
        }
    }

    /**
     * 从环境中获取脚本引擎 [ScriptEngine]
     */
    fun getEngine(environment: ScriptEnvironment): ScriptEngine {
        // 获取脚本引擎
        val engine = environment.context.fetch(this) { newEngine() }
        // 环境的绑定键 (直接导入全局域多好)
        engine.setBindings(environment.bindings, ScriptContext.GLOBAL_SCOPE)
        return engine
    }

    /**
     * 创建脚本引擎实例
     */
    @JvmStatic
    fun newEngine(): ScriptEngine {
        // 创建脚本引擎
        val engine = scriptEngineFactory?.getScriptEngine(
            arrayOf("-Dnashorn.args=--language=es6"), this::class.java.classLoader
        ) ?: throw NullPointerException("Cannot find ScriptEngine for JavaScript(Nashorn) Language")
        engine.context = object : ImportedScriptContext(engine.context) {
            override fun getImport(name: String): Any? {
                val clazz = super.getImport(name)
                // 转化成 StaticClass 便于 Nashorn 使用
                return clazz?.let { jdk.dynalink.beans.StaticClass.forClass(it as Class<*>) }
            }
        }
        return engine
    }

    val scriptEngineFactory by lazy {
        ScriptManager.engineManager.engineFactories.find {
            it.engineName == "OpenJDK Nashorn"
        } as? NashornScriptEngineFactory
    }

}
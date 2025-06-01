package cn.fd.ratziel.module.script.lang

import cn.fd.ratziel.module.script.ScriptManager
import cn.fd.ratziel.module.script.api.ScriptEnvironment
import cn.fd.ratziel.module.script.impl.EnginedScriptExecutor
import cn.fd.ratziel.module.script.impl.ImportedScriptContext
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory
import javax.script.ScriptContext
import javax.script.ScriptEngine

/**
 * NashornScriptExecutor
 *
 * @author TheFloodDragon
 * @since 2025/4/26 09:33
 */
object NashornScriptExecutor : EnginedScriptExecutor() {

    init {
        ScriptManager.loadDependencies("nashorn")
    }

    val scriptEngineFactory by lazy {
        ScriptManager.engineManager.engineFactories.find {
            it.engineName == "OpenJDK Nashorn"
        } as? NashornScriptEngineFactory
    }

    /**
     * 创建脚本引擎实例
     */
    override fun newEngine(): ScriptEngine {
        val engine = scriptEngineFactory?.getScriptEngine(
            arrayOf("-Dnashorn.args=--language=es6"), this::class.java.classLoader
        ) ?: throw NullPointerException("Cannot find ScriptEngine for JavaScript(Nashorn) Language")
        return engine
    }

    /**
     * 创建 [ScriptContext]
     * (为了避免引擎沾染环境, 所以要导入绑定键而不是直接用环境上下文)
     */
    override fun createContext(engine: ScriptEngine, environment: ScriptEnvironment): ScriptContext {
        // 导入环境的引擎绑定键
        val engineBindings = engine.createBindings() // 需要通过脚本引擎创建
        engineBindings.putAll(environment.bindings)
        environment.bindings = engineBindings // 更新环境的绑定键 (以便脚本内部上下文的继承)

        // 创建导入的脚本上下文
        val context = object : ImportedScriptContext(environment.context) {
            override fun getImport(name: String): Any? {
                val clazz = super.getImport(name)
                // 转化成 StaticClass 便于 Nashorn 使用
                return clazz?.let { jdk.dynalink.beans.StaticClass.forClass(it as Class<*>) }
            }
        }

        // 返回最终的脚本上下文
        return context
    }

}
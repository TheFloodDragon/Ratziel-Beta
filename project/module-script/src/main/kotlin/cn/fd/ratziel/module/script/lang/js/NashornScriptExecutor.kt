package cn.fd.ratziel.module.script.lang.js

import cn.fd.ratziel.module.script.ScriptManager
import cn.fd.ratziel.module.script.api.ScriptEnvironment
import cn.fd.ratziel.module.script.impl.EnginedScriptExecutor
import cn.fd.ratziel.module.script.impl.ImportedScriptContext
import cn.fd.ratziel.module.script.internal.NonStrictCompilation
import cn.fd.ratziel.module.script.lang.JavaScriptLang
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory
import javax.script.ScriptContext
import javax.script.ScriptEngine

/**
 * NashornScriptExecutor
 *
 * @author TheFloodDragon
 * @since 2025/4/26 09:33
 */
object NashornScriptExecutor : EnginedScriptExecutor(), NonStrictCompilation {

    /**
     * 创建脚本引擎实例
     */
    override fun newEngine(): ScriptEngine {
        // 创建脚本引擎
        val engine = scriptEngineFactory?.getScriptEngine(
            arrayOf("-Dnashorn.args=--language=es6"), this::class.java.classLoader
        ) ?: throw NullPointerException("Cannot find ScriptEngine for JavaScript(Nashorn) Language")
        // 加载全局扩展脚本
        for ((script,_) in JavaScriptLang.globalScripts) {
            engine.eval(script)
        }
        return engine
    }

    /**
     * 创建 [ScriptContext]
     */
    override fun createContext(engine: ScriptEngine, environment: ScriptEnvironment): ScriptContext {
        val context = super.createContext(engine, environment)
        // 返回导入的脚本上下文
        return object : ImportedScriptContext(context) {
            override fun getImport(name: String): Any? {
                val clazz = super.getImport(name)
                // 转化成 StaticClass 便于 Nashorn 使用
                return clazz?.let { jdk.dynalink.beans.StaticClass.forClass(it as Class<*>) }
            }
        }
    }

    val scriptEngineFactory by lazy {
        ScriptManager.engineManager.engineFactories.find {
            it.engineName == "OpenJDK Nashorn"
        } as? NashornScriptEngineFactory
    }

}
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
class NashornScriptExecutor : EnginedScriptExecutor() {

    override val engine: ScriptEngine by lazy {
        newEngine().apply {
            // 设置脚本引擎的全局绑定键
            setBindings(ScriptManager.Global.globalBindings, ScriptContext.GLOBAL_SCOPE)
        }
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

    companion object {

        val scriptEngineFactory by lazy {
            ScriptManager.engineManager.engineFactories.find {
                it.engineName == "OpenJDK Nashorn"
            } as? NashornScriptEngineFactory
        }

        /**
         * 创建脚本引擎实例
         */
        fun newEngine(): ScriptEngine {
            val engine = scriptEngineFactory?.getScriptEngine(
                arrayOf("-Dnashorn.args=--language=es6"), this::class.java.classLoader
            ) ?: throw NullPointerException("Cannot find ScriptEngine for JavaScript(Nashorn) Language")
            return engine
        }

    }

}
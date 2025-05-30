package cn.fd.ratziel.module.script.lang

import cn.fd.ratziel.module.script.api.ScriptEnvironment
import cn.fd.ratziel.module.script.impl.EnginedScriptExecutor
import cn.fd.ratziel.module.script.impl.ImportedScriptContext
import cn.fd.ratziel.module.script.internal.Initializable
import jdk.internal.dynalink.beans.StaticClass
import taboolib.library.configuration.ConfigurationSection
import javax.script.ScriptContext
import javax.script.ScriptEngine

/**
 * JavaScriptExecutor
 *
 * @author TheFloodDragon
 * @since 2025/4/26 9:37
 */
object JavaScriptExecutor : EnginedScriptExecutor(), Initializable {

    private lateinit var engine: EnginedScriptExecutor

    override fun initialize(settings: ConfigurationSection) {
        // 读取引擎
        val selected = settings.getString("engine")
        // 创建脚本执行器
        val engine = when (selected?.lowercase()) {
            "nashorn" -> NashornScriptExecutor
            "graaljs" -> GraalJsScriptExecutor
            else -> NashornScriptExecutor
        }
        this.engine = engine
    }

    override fun getEngine() = engine.getEngine()

    /**
     * 创建 [ScriptContext]
     * (为了避免引擎沾染环境, 所以要导入绑定键而不是直接用环境上下文)
     */
    override fun createContext(engine: ScriptEngine, environment: ScriptEnvironment): ScriptContext {
        // 创建导入的脚本上下文 (Nashorn和GraalJs要将类 Class 转化为 StaticClass, 以便脚本内部可以直接使用)
        val context = object : ImportedScriptContext() {
            override fun getImport(name: String): Any? {
                val clazz = super.getImport(name) as? Class<*>
                return clazz?.let { StaticClass.forClass(it) }
            }
        }

        // 导入环境的引擎绑定键
        val engineBindings = engine.createBindings() // 需要通过脚本引擎创建, 以便脚本内部上下文的继承
        engineBindings.putAll(environment.bindings)

        // 导入全局绑定键
        val globalBindings = environment.context.getBindings(ScriptContext.GLOBAL_SCOPE) // 导入环境的全局绑定键

        // 设置绑定键
        context.setBindings(engineBindings, ScriptContext.ENGINE_SCOPE)
        context.setBindings(globalBindings, ScriptContext.GLOBAL_SCOPE)
        return context
    }

}
package cn.fd.ratziel.module.script.lang

import cn.fd.ratziel.module.script.impl.EnginedScriptExecutor
import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine
import org.graalvm.polyglot.Context
import javax.script.ScriptEngine

/**
 * GraalJsScriptExecutor
 *
 * @author TheFloodDragon
 * @since 2025/4/26 09:56
 */
object GraalJsScriptExecutor : EnginedScriptExecutor() {

    /**
     * 创建一个新的 [Context.Builder]
     */
    val builder: Context.Builder by lazy {
        Context.newBuilder("js")
            .hostClassLoader(this::class.java.classLoader)
            .allowAllAccess(true) // 全开了算了
            .allowExperimentalOptions(true)
            .option("js.ecmascript-version", "latest")
            .option("js.nashorn-compat", "true") // Nashorn 兼容模式
    }

    override fun newEngine(): ScriptEngine {
        val engine = GraalJSScriptEngine.create(null, builder)
        // 导入要导入的包和类
        engine.eval(importingScript)
        // 加载全局扩展脚本
        for (script in JavaScriptLang.globalScripts) {
            engine.eval(script)
        }
        return engine
    }

    /**
     * 用来导入包和类的脚本
     */
    private val importingScript by lazy {
        this::class.java.classLoader.getResourceAsStream("script-default/graaljs.scriptengine.js")!!.reader(Charsets.UTF_8)
    }

}
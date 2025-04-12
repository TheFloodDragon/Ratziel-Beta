package cn.fd.ratziel.module.script.lang

import cn.fd.ratziel.module.script.ScriptManager
import cn.fd.ratziel.module.script.api.CompilableScript
import cn.fd.ratziel.module.script.api.ScriptContent
import cn.fd.ratziel.module.script.api.ScriptEnvironment
import cn.fd.ratziel.module.script.api.ScriptExecutor
import cn.fd.ratziel.module.script.impl.CacheableScriptContent
import cn.fd.ratziel.module.script.internal.Initializable
import taboolib.common.env.RuntimeEnv
import taboolib.common.platform.function.warning
import taboolib.library.configuration.ConfigurationSection
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import javax.script.*

/**
 * JavaScriptExecutor
 *
 * @author TheFloodDragon
 * @since 2024/10/4 20:05
 */
object JavaScriptExecutor : ScriptExecutor, Initializable {

    /**
     * 全局绑定键列表
     */
    val globalBindings by lazy { SimpleBindings(ConcurrentHashMap()) }

    /**
     * 构建脚本
     * @param compile 是否启用编译
     * @param async 若编译启用, 是否异步编译
     */
    fun build(script: String, compile: Boolean = true, async: Boolean = true): CacheableScriptContent {
        val sc = CacheableScriptContent(script, this)
        if (compile && sc.future == null) {
            sc.future = if (async) {
                CompletableFuture.supplyAsync { compile(script) }
            } else CompletableFuture.completedFuture(compile(script))
        }
        return sc
    }

    override fun build(script: String) = build(script, compile = true, async = true)

    override fun evaluate(script: ScriptContent, environment: ScriptEnvironment): Any? {
        if (script is CompilableScript) {
            val compiled = script.compiled
            if (compiled != null) return compiled.eval(environment.context)
        }
        return newEngine().eval(script.content, environment.context)
    }

    /**
     * 编译脚本
     */
    fun compile(script: String): CompiledScript? {
        try {
            return (newEngine() as Compilable).compile(script)
        } catch (e: Exception) {
            warning("Cannot compile script by '$this' ! Script content: $script")
            e.printStackTrace()
        }
        return null
    }

    fun newEngine(): ScriptEngine {
        val engine = ScriptManager.engineManager.getEngineByName("js")
            ?: throw NullPointerException("Cannot find ScriptEngine for JavaScript Language")
        // 设置全局绑定键
        engine.setBindings(globalBindings, ScriptContext.GLOBAL_SCOPE)
        return engine
    }

    override fun initialize(settings: ConfigurationSection) {
        RuntimeEnv.ENV_DEPENDENCY.loadFromLocalFile(this::class.java.classLoader.getResource("META-INF/dependencies/nashorn.json"))
    }

}
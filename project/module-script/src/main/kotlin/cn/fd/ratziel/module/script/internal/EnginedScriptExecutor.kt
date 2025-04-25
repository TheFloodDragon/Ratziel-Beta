package cn.fd.ratziel.module.script.internal

import cn.fd.ratziel.module.script.api.CompilableScript
import cn.fd.ratziel.module.script.api.ScriptContent
import cn.fd.ratziel.module.script.api.ScriptEnvironment
import cn.fd.ratziel.module.script.api.ScriptExecutor
import cn.fd.ratziel.module.script.impl.CacheableScriptContent
import cn.fd.ratziel.module.script.lang.JavaScriptExecutor
import taboolib.common.platform.function.warning
import java.util.concurrent.CompletableFuture
import javax.script.Compilable
import javax.script.CompiledScript
import javax.script.ScriptEngine

/**
 * EnginedScriptExecutor
 *
 * @author TheFloodDragon
 * @since 2025/4/25 16:40
 */
abstract class EnginedScriptExecutor : ScriptExecutor {

    /**
     * 创建一个新的 [ScriptEngine]
     */
    abstract fun newEngine(): ScriptEngine

    /**
     * 执行脚本
     */
    override fun evaluate(script: ScriptContent, environment: ScriptEnvironment): Any? {
        if (script is CompilableScript) {
            val compiled = script.compiled
            if (compiled != null) return compiled.eval(environment.context)
        }
        return JavaScriptExecutor.newEngine().eval(script.content, environment.context)
    }

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

    override fun build(script: String): ScriptContent {
        return build(script, compile = false, async = true)
    }

    /**
     * 编译脚本
     */
    fun compile(script: String): CompiledScript? {
        try {
            return (JavaScriptExecutor.newEngine() as Compilable).compile(script)
        } catch (e: Exception) {
            warning("Cannot compile script by '$this' ! Script content: $script")
            e.printStackTrace()
        }
        return null
    }

}
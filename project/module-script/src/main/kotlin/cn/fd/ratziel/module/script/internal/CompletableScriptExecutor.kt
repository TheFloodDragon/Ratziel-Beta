package cn.fd.ratziel.module.script.internal

import cn.fd.ratziel.module.script.api.ScriptContent
import cn.fd.ratziel.module.script.api.ScriptEnvironment
import cn.fd.ratziel.module.script.api.ScriptExecutor
import cn.fd.ratziel.module.script.impl.CompletableScript
import taboolib.common.platform.function.warning
import java.util.concurrent.CompletableFuture

/**
 * CompletableScriptExecutor
 *
 * @author TheFloodDragon
 * @since 2025/4/26 10:49
 */
abstract class CompletableScriptExecutor<T : Any> : ScriptExecutor {

    /**
     * 直接评估脚本
     */
    abstract fun evalDirectly(script: String, environment: ScriptEnvironment): Any?

    /**
     * 编译脚本
     */
    abstract fun compile(script: String): T

    /**
     * 评估编译后的脚本
     */
    abstract fun evalCompiled(script: T, environment: ScriptEnvironment): Any?

    /**
     * 执行脚本
     */
    override fun evaluate(script: ScriptContent, environment: ScriptEnvironment): Any? {
        if (script is CompletableScript<*>) {
            @Suppress("UNCHECKED_CAST")
            val compiled = script.completed as? T
            if (compiled != null) return evalCompiled(compiled, environment)
        }
        return evalDirectly(script.content, environment)
    }

    /**
     * 构建脚本
     * @param compile 是否启用编译
     * @param async 若编译启用, 是否异步编译
     */
    fun build(script: String, compile: Boolean = true, async: Boolean = true): CompletableScript<T> {
        val sc = CompletableScript<T>(script, this)
        if (compile && sc.completed == null) {
            val func = Runnable {
                try {
                    val compiled = this.compile(script)
                    sc.complete(compiled)
                } catch (e: Exception) {
                    warning("Cannot compile script by '$this' ! Script content: $script")
                    e.printStackTrace()
                }
            }
            // 异步 & 同步编译
            if (async) {
                CompletableFuture.runAsync(func)
            } else func.run()
        }
        return sc
    }

    override fun build(script: String): ScriptContent {
        return build(script, compile = true, async = true)
    }

}
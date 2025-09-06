package cn.fd.ratziel.module.script.impl

import cn.fd.ratziel.core.functional.replenish
import cn.fd.ratziel.module.script.api.ScriptContent
import cn.fd.ratziel.module.script.api.ScriptEnvironment
import java.util.concurrent.CompletableFuture

/**
 * EnginedScriptExecutor
 *
 * @author TheFloodDragon
 * @since 2025/9/6 21:11
 */
abstract class EnginedScriptExecutor<T : Any> : CompilableScriptExecutor<T>() {

    /**
     * 预热环境 - 初始化脚本引擎
     */
    abstract fun preheat(environment: ScriptEnvironment)

    override fun evaluate(script: ScriptContent, environment: ScriptEnvironment): Any? {
        // 处理特殊脚本 (设置环境)
        if (script is EnginedScriptContent<*>) {
            script.importTo(environment)
        }
        return super.evaluate(script, environment)
    }

    override fun build(script: String, environment: ScriptEnvironment): ScriptContent {
        val script = super.build(script, environment)
        if (script is CompiledScript<*> && script.executor == this) {
            @Suppress("UNCHECKED_CAST")
            return EnginedScriptContent(script as CompiledScript<T>, this)
        } else return script
    }

    protected class EnginedScriptContent<T : Any>(delegate: CompiledScript<T>, executor: EnginedScriptExecutor<T>) : CompiledScript<T>(delegate) {

        /**
         * 脚本引擎补充器 (提高并行执行多编译脚本的性能)
         */
        private val engineReplenishing: CompletableFuture<ScriptEnvironment> by replenish {
            CompletableFuture.supplyAsync {
                ScriptEnvironmentImpl().also {
                    // 预热环境
                    executor.preheat(it)
                }
            }
        }

        /**
         * 将补充的预热过的环境导入到 [environment] 里
         *
         * 要求存在 [ScriptEnvironment] 里的 脚本引擎必须以 [executor] 为键
         */
        fun importTo(environment: ScriptEnvironment) {
            // 没有初始化的环境
            if (environment.context.contents.isEmpty() || environment.context.fetchOrNull<Any>(this.executor) == null) {
                // 导入补充的预热过的环境
                val enginedEnvironment = engineReplenishing.get()
                // 设置上下文
                environment.context = enginedEnvironment.context
                // 导入绑定键
                if (enginedEnvironment.bindings.isNotEmpty()) {
                    environment.bindings.putAll(enginedEnvironment.bindings)
                }
            }
        }

    }

}
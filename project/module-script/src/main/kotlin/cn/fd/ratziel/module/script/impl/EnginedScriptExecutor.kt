package cn.fd.ratziel.module.script.impl

import cn.fd.ratziel.core.functional.replenish
import cn.fd.ratziel.module.script.api.ScriptContent
import cn.fd.ratziel.module.script.api.ScriptEnvironment
import cn.fd.ratziel.module.script.api.ScriptSource
import java.util.concurrent.CompletableFuture

/**
 * EnginedScriptExecutor
 *
 * @author TheFloodDragon
 * @since 2025/9/6 21:11
 */
abstract class EnginedScriptExecutor<T : Any> : CompilableScriptExecutor<T> {

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

    override fun build(source: ScriptSource, environment: ScriptEnvironment, compile: Boolean): ScriptContent {
        val script = super.build(source, environment)
        if (script is CompiledScript<*> && script.executor == this) {
            @Suppress("UNCHECKED_CAST")
            return EnginedScriptContent(script as CompiledScript<T>, environment, this)
        } else return script
    }

    protected class EnginedScriptContent<T : Any>(
        delegate: CompiledScript<T>,
        /** 原始脚本环境 **/
        originalEnvironment: ScriptEnvironment,
        /** 引擎执行器 **/
        executor: EnginedScriptExecutor<T>,
    ) : CompiledScript<T>(delegate) {

        /**
         * 脚本引擎补充器 (提高并行执行多编译脚本的性能)
         */
        private val engineReplenishing: CompletableFuture<ScriptEnvironment> by replenish {
            CompletableFuture.supplyAsync {
                ScriptEnvironment(originalEnvironment.bindings).also {
                    // 导入原始脚本环境里的东西
                    it.context.putAll(originalEnvironment.context)
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
            if (environment.context.fetchOrNull<Any>(this.executor) == null) {
                // 导入补充的预热过的环境
                val enginedEnvironment = engineReplenishing.get()
                // 导入上下文
                environment.context.putAll(enginedEnvironment.context)
                // 导入绑定键
                if (enginedEnvironment.bindings.isNotEmpty()) {
                    environment.bindings.putAll(enginedEnvironment.bindings)
                }
            }
        }

    }

}
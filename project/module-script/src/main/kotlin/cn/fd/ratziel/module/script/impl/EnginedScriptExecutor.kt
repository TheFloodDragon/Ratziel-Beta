package cn.fd.ratziel.module.script.impl

import cn.fd.ratziel.core.functional.replenish
import cn.fd.ratziel.module.script.api.ScriptEnvironment
import java.util.concurrent.CompletableFuture
import kotlin.concurrent.getOrSet

/**
 * EnginedScriptExecutor
 *
 * @author TheFloodDragon
 * @since 2025/10/3 22:11
 */
abstract class EnginedScriptExecutor<T : Any, E : Any> : CompilableScriptExecutor<EnginedScriptExecutor.CachedScript<T, E>> {

    /**
     * 通过编译时的脚本环境, 提前预热出 脚本引擎实例
     */
    abstract fun initRuntime(environment: ScriptEnvironment): E

    /**
     * 缓存的脚本
     */
    class CachedScript<T : Any, E : Any>(
        /**
         * 编译后的脚本
         */
        val script: T,
        /**
         * 原始脚本环境 (编译时给的)
         */
        val originEnvironment: ScriptEnvironment,
        /**
         * 脚本执行器
         */
        val executor: EnginedScriptExecutor<T, E>,
    ) {

        /**
         * 脚本引擎补充器 (提前预热脚本以提高运行性能)
         */
        private val engineReplenishing by replenish {
            CompletableFuture.supplyAsync {
                // 继承编译时传入的绑定键, 预热脚本引擎
                executor.initRuntime(originEnvironment)
            }
        }

        /**
         * [ThreadLocal] 存储每个线程的 脚本引擎实例
         * 同一个线程共享一个 脚本引擎实例
         */
        private val local = ThreadLocal<E>()

        /**
         * 获取当前线程的 预热后的 脚本引擎实例
         */
        fun get(): E {
            return local.getOrSet { engineReplenishing.get() }
        }

    }

}
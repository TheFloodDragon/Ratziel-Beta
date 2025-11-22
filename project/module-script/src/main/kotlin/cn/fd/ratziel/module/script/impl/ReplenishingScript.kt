package cn.fd.ratziel.module.script.impl

import cn.fd.ratziel.core.functional.replenish
import cn.fd.ratziel.module.script.api.ScriptEnvironment
import cn.fd.ratziel.module.script.api.ScriptSource
import cn.fd.ratziel.module.script.api.ValuedCompiledScript
import java.util.concurrent.CompletableFuture

/**
 * ReplenishingScript
 *
 * @author TheFloodDragon
 * @since 2025/11/1 20:54
 */
abstract class ReplenishingScript<C, E : Any>(
    /**
     * 编译后的脚本
     */
    script: C,
    /**
     * 编译时的脚本环境
     */
    val compilationEnv: ScriptEnvironment,
    source: ScriptSource,
) : ValuedCompiledScript<C>(script, source) {

    /**
     * 脚本引擎补充器 (提前预热脚本以提高运行性能)
     */
    private val preheatedEngineReplenishing by replenish {
        CompletableFuture.supplyAsync { this.preheat() }
    }

    /**
     * 弹出一个预热后的脚本引擎实例
     */
    private fun pop(): E = preheatedEngineReplenishing.get()

    /**
     * 提前预热出一个 脚本引擎实例
     * @return 预热出来的脚本引擎实例
     */
    abstract fun preheat(): E

    /**
     * 初始化运行时环境中的脚本引擎
     */
    open fun initRuntime(engine: E, runtimeEnv: ScriptEnvironment) = Unit

    /**
     * 使用脚本引擎评估此编译后的脚本
     */
    abstract fun eval(engine: E): Any?

    final override fun eval(environment: ScriptEnvironment): Any? {
        // 获取脚本引擎实例:
        // 用脚本语言类型做钥匙 (key), 确保同一语言的脚本在同一环境中使用同一引擎实例.
        val engine: E = environment.runningState.fetch(source.language) {
            pop().also { initRuntime(it, environment) }
        }
        // 调用评估函数
        return this.eval(engine)
    }

}
package cn.fd.ratziel.module.script.impl

import cn.fd.ratziel.module.script.api.ScriptEnvironment
import javax.script.Compilable
import javax.script.CompiledScript
import javax.script.ScriptContext
import javax.script.ScriptEngine

/**
 * EnginedScriptExecutor
 *
 * @author TheFloodDragon
 * @since 2025/4/25 16:40
 */
abstract class EnginedScriptExecutor : CompletableScriptExecutor<CompiledScript>() {

    /**
     * 创建脚本引擎实例
     */
    abstract fun newEngine(): ScriptEngine

    override fun evalDirectly(script: String, environment: ScriptEnvironment): Any? {
        val engine = newEngine()
        return engine.eval(script, createContext(engine, environment))
    }

    /**
     * 编译原始脚本
     *
     * @param script 原始脚本
     */
    override fun compile(script: String): CompiledScript {
        val engine = newEngine()
        engine.context = createContext(engine, SimpleScriptEnvironment())
        return (engine as Compilable).compile(script)
    }

    override fun evalCompiled(script: CompiledScript, environment: ScriptEnvironment): Any? {
        return synchronized(script.engine) { // 避免并发错误
            script.eval(createContext(script.engine, environment))
        }
    }

    /**
     * 创建脚本上下文
     *
     * @param engine 脚本引擎
     * @param environment 脚本环境
     */
    open fun createContext(engine: ScriptEngine, environment: ScriptEnvironment): ScriptContext {
        val context = engine.context
        // 获取执行器上下文
        val contextualBindings = environment.attachedContext.fetch(this) {
            context.getBindings(ScriptContext.ENGINE_SCOPE) ?: engine.createBindings()
        }

        // 执行器上下文的绑定键
        context.setBindings(contextualBindings, ScriptContext.ENGINE_SCOPE)
        // 环境的绑定键 (直接导入全局域多好)
        context.setBindings(environment.bindings, ScriptContext.GLOBAL_SCOPE)

        // 返回引擎上下文
        return context
    }

}
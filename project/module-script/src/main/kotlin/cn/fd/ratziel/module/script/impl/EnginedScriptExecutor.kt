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
     * 脚本引擎实例
     */
    abstract val engine: ScriptEngine

    @Synchronized
    override fun evalDirectly(script: String, environment: ScriptEnvironment): Any? {
        return engine.eval(script, createContext(engine, environment))
    }

    /**
     * 编译原始脚本
     *
     * @param script 原始脚本
     * @param environment 脚本环境 (默认情况下不被使用, 若需要导入环境, 请重写此方法)
     */
    @Synchronized
    override fun compile(script: String, environment: ScriptEnvironment): CompiledScript {
        return (engine as Compilable).compile(script)
    }

    @Synchronized
    override fun evalCompiled(script: CompiledScript, environment: ScriptEnvironment): Any? {
        return script.eval(createContext(script.engine, environment))
    }

    /**
     * 创建脚本上下文
     *
     * @param engine 脚本引擎
     * @param environment 脚本环境
     */
    @Synchronized
    open fun createContext(engine: ScriptEngine, environment: ScriptEnvironment): ScriptContext {
        // 环境的绑定键
        val environmentBindings = environment.bindings
        if (environmentBindings.isNotEmpty()) {
            // 导入环境的绑定键
            engine.context.getBindings(ScriptContext.ENGINE_SCOPE)
                .putAll(environmentBindings)
        }
        // 返回引擎上下文
        return engine.context
    }

}
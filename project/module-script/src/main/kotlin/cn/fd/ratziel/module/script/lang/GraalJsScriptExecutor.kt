package cn.fd.ratziel.module.script.lang

import cn.fd.ratziel.module.script.ScriptManager
import cn.fd.ratziel.module.script.api.ScriptEnvironment
import cn.fd.ratziel.module.script.impl.EnginedScriptExecutor
import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine
import org.graalvm.polyglot.Context
import javax.script.ScriptContext
import javax.script.ScriptEngine
import javax.script.SimpleScriptContext

/**
 * GraalJsScriptExecutor
 *
 * @author TheFloodDragon
 * @since 2025/4/26 09:56
 */
object GraalJsScriptExecutor : EnginedScriptExecutor() {

    init {
        ScriptManager.loadDependencies("graaljs")
    }

    /**
     * 创建一个新的 [Context.Builder]
     */
    val builder: Context.Builder by lazy {
        Context.newBuilder("js")
            .hostClassLoader(this::class.java.classLoader)
            .allowAllAccess(true) // 全开了算了
            .allowExperimentalOptions(true)
            .option("js.nashorn-compat", "true") // Nashorn 兼容模式
    }

    override fun newEngine(): ScriptEngine {
        return GraalJSScriptEngine.create(null, builder)
    }

    /**
     * 创建 [ScriptContext]
     * (为了避免引擎沾染环境, 所以要导入绑定键而不是直接用环境上下文)
     */
    override fun createContext(engine: ScriptEngine, environment: ScriptEnvironment): ScriptContext {
        val context = SimpleScriptContext()

        // 导入环境的引擎绑定键
        val engineBindings = engine.createBindings() // 需要通过脚本引擎创建, 以便脚本内部上下文的继承
        engineBindings.putAll(environment.bindings)
        context.setBindings(engineBindings, ScriptContext.ENGINE_SCOPE)

        // 导入全局
        context.setBindings(environment.context.getBindings(ScriptContext.GLOBAL_SCOPE), ScriptContext.GLOBAL_SCOPE)

        // 导入要导入的包和类
        engine.eval(
            """
            var oldNoSuchProperty = Object.__noSuchProperty__;
            Object.defineProperty(this, "__noSuchProperty__", {
                writable: true, configurable: true, enumerable: false,
                value: function(name) {
                    'use strict';
                    var global = Java.type('cn.fd.ratziel.module.script.ScriptManager.Global');
                    var type = global.getImportedClass(name)
                    if (type) {
                        return Packages.jdk.dynalink.beans.StaticClass.forClass(type);
                    }
                    if (oldNoSuchProperty) {
                        return oldNoSuchProperty.call(this, name);
                    } else {
                        if (this === undefined) {
                            throw new ReferenceError(name + " is not defined");
                        } else {
                            return undefined;
                        }
                    }
                }
            });
        """.trimIndent(), engineBindings
        )

        return context
    }

}
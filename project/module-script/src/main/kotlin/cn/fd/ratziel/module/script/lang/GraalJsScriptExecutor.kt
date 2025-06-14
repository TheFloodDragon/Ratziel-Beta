package cn.fd.ratziel.module.script.lang

import cn.fd.ratziel.module.script.ScriptManager
import cn.fd.ratziel.module.script.ScriptType
import cn.fd.ratziel.module.script.impl.EnginedScriptExecutor
import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine
import org.graalvm.polyglot.Context
import javax.script.ScriptContext
import javax.script.ScriptEngine

/**
 * GraalJsScriptExecutor
 *
 * @author TheFloodDragon
 * @since 2025/4/26 09:56
 */
class GraalJsScriptExecutor : EnginedScriptExecutor() {

    /**
     * 创建一个新的 [Context.Builder]
     */
    val builder: Context.Builder by lazy {
        Context.newBuilder("js")
            .hostClassLoader(this::class.java.classLoader)
            .allowAllAccess(true) // 全开了算了
            .allowExperimentalOptions(true)
            .option("js.ecmascript-version", "latest")
            .option("js.nashorn-compat", "true") // Nashorn 兼容模式
    }

    override val engine: ScriptEngine by lazy {
        GraalJSScriptEngine.create(null, builder).apply {
            // 设置脚本引擎的全局绑定键
            setBindings(ScriptManager.Global.globalBindings, ScriptContext.GLOBAL_SCOPE)
            // 导入要导入的包和类
            eval(
                """
                var oldNoSuchProperty = Object.__noSuchProperty__;
                Object.defineProperty(this, "__noSuchProperty__", {
                    writable: true, configurable: true, enumerable: false,
                    value: function(name) {
                        'use strict';
                        var global = Java.type('cn.fd.ratziel.module.script.ScriptManager.Global');
                        var clazz = global.getImportedClass(name)
                        if (clazz) {
                            return Java.type(clazz.name);
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
             """.trimIndent()
            )
            // 加载全局扩展脚本
            for (script in JavaScriptExecutor.getGlobalScripts()) eval(script)
        }
    }

    override fun getLanguage() = ScriptType.JAVASCRIPT

}
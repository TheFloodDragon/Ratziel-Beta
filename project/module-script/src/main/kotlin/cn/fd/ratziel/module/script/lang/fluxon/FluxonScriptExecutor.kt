package cn.fd.ratziel.module.script.lang.fluxon

import cn.fd.ratziel.core.util.randomUuid
import cn.fd.ratziel.module.script.api.*
import cn.fd.ratziel.module.script.conf.ScriptConfiguration
import cn.fd.ratziel.module.script.conf.ScriptConfigurationKeys
import cn.fd.ratziel.module.script.conf.scriptCaching
import org.tabooproject.fluxon.Fluxon
import org.tabooproject.fluxon.compiler.CompilationContext
import org.tabooproject.fluxon.compiler.CompileResult
import org.tabooproject.fluxon.interpreter.bytecode.FluxonClassLoader
import org.tabooproject.fluxon.parser.ParseResult
import org.tabooproject.fluxon.runtime.Environment
import org.tabooproject.fluxon.runtime.FluxonRuntime
import org.tabooproject.fluxon.runtime.RuntimeScriptBase
import taboolib.common.io.digest

/**
 * FluxonScriptExecutor
 *
 * @author TheFloodDragon
 * @since 2025/11/1 22:06
 */
class FluxonScriptExecutor : IntegratedScriptExecutor() {

    /**
     * 脚本配置
     */
    var configuration: ScriptConfiguration = ScriptConfiguration.Default

    override fun compile(source: ScriptSource, environment: ScriptEnvironment): CompiledScript {
        // 编译配置
        val compilation = CompilationContext(source.content, source.name ?: "<main>")

        // 可选择多级缓存编译
        return when (configuration[ScriptConfigurationKeys.scriptCaching]) {
            // 字节码编译
            in 2..Int.MAX_VALUE -> object : ValuedCompiledScript<CompileResult>(
                Fluxon.compile(
                    compilation.source,
                    compilation.fileName + randomUuid().digest(),
                    environment.asFluxonEnv(),
                ),
                source, this
            ) {
                val definedClass = script.defineClass(FluxonClassLoader())
                override fun eval(environment: ScriptEnvironment): Any? {
                    val runtime = definedClass.newInstance() as RuntimeScriptBase
                    return runtime.eval(environment.asFluxonEnv())
                }
            }
            // AST 编译
            else -> object : ValuedCompiledScript<List<ParseResult>>(
                Fluxon.parse(environment.asFluxonEnv(), compilation),
                source, this
            ) {
                override fun eval(environment: ScriptEnvironment): Any? {
                    return Fluxon.eval(script, environment.asFluxonEnv())
                }
            }
        }
    }

    override fun evaluate(script: ScriptContent, environment: ScriptEnvironment): Any? {
        return Fluxon.eval(script.content, environment.asFluxonEnv())
    }

    fun ScriptEnvironment.asFluxonEnv(): Environment {
        return FluxonRuntime.getInstance().newEnvironment().apply {
            rootVariables.putAll(bindings) // 导入绑定键
        }
    }

    override fun compiler() = FluxonScriptExecutor()
    override fun evaluator() = FluxonScriptExecutor()

    companion object {

        /**
         * 默认脚本实例
         */
        @JvmField
        val DEFAULT = FluxonScriptExecutor()

    }

}
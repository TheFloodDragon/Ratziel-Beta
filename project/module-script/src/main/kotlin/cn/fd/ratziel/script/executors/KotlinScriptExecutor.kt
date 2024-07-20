package cn.fd.ratziel.script.executors

import cn.fd.ratziel.core.env.CoreEnv
import cn.fd.ratziel.script.api.ScriptContent
import cn.fd.ratziel.script.api.ScriptEnvironment
import cn.fd.ratziel.script.impl.CompilableScriptExecutor
import taboolib.common.env.RuntimeDependencies
import taboolib.common.env.RuntimeDependency
import javax.script.Compilable
import javax.script.CompiledScript
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

/**
 * KotlinScriptExecutor
 *
 * @author TheFloodDragon
 * @since 2024/7/14 21:41
 */
@RuntimeDependencies(
    RuntimeDependency(
        value = "!org.jetbrains.kotlin:kotlin-reflect:" + CoreEnv.KOTLIN_VERSION,
        test = "!kotlin.reflect.jvm.ReflectLambdaKt",
        transitive = false
    ),
    RuntimeDependency(
        value = "!org.jetbrains.kotlin:kotlin-compiler:" + CoreEnv.KOTLIN_VERSION,
        transitive = false
    ),
    RuntimeDependency(
        value = "!org.jetbrains.kotlin:kotlin-script-runtime:" + CoreEnv.KOTLIN_VERSION,
        transitive = false
    ),
    RuntimeDependency(
        value = "!org.jetbrains.kotlin:kotlin-scripting-common:" + CoreEnv.KOTLIN_VERSION,
        transitive = false
    ),
    RuntimeDependency(
        value = "!org.jetbrains.kotlin:kotlin-scripting-jvm:" + CoreEnv.KOTLIN_VERSION,
        transitive = false
    ),
    RuntimeDependency(
        value = "!org.jetbrains.kotlin:kotlin-scripting-jvm-host-unshaded:" + CoreEnv.KOTLIN_VERSION,
        transitive = false
    ),
    RuntimeDependency(
        value = "!org.jetbrains.kotlin:kotlin-scripting-compiler:" + CoreEnv.KOTLIN_VERSION,
        transitive = false
    ),
    RuntimeDependency(
        value = "!org.jetbrains.kotlin:kotlin-scripting-compiler-impl:" + CoreEnv.KOTLIN_VERSION,
        transitive = false
    ),
    RuntimeDependency(
        value = "!org.jetbrains.intellij.deps:trove4j:1.0.20200330",
        test = "!gnu.trove.TObjectHashingStrategy",
        transitive = false
    )
)
object KotlinScriptExecutor : CompilableScriptExecutor {

    override fun compile(script: String): CompiledScript {
        return (newEngine() as Compilable).compile(script)
    }

    override fun evaluate(script: ScriptContent, environment: ScriptEnvironment): Any? {
        return newEngine().eval(script.content, environment.bindings)
    }

    fun newEngine(): ScriptEngine =
        ScriptEngineManager(this::class.java.classLoader).getEngineByName("kotlin")

}
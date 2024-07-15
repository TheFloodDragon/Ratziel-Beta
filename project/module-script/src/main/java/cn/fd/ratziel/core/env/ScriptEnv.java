package cn.fd.ratziel.core.env;

import taboolib.common.env.RuntimeDependencies;
import taboolib.common.env.RuntimeDependency;

/**
 * ScriptEnv
 *
 * @author TheFloodDragon
 * @since 2024/7/14 14:54
 */
@RuntimeDependencies({
        // Kotlin Script
        @RuntimeDependency(
                value = "!org.jetbrains.kotlin:kotlin-reflect:" + CoreEnv.KOTLIN_VERSION,
                test = "!kotlin.reflect.jvm.ReflectLambdaKt",
                transitive = false
        ),
        @RuntimeDependency(
                value = "!org.jetbrains.kotlin:kotlin-compiler:" + CoreEnv.KOTLIN_VERSION,
                transitive = false
        ),
        @RuntimeDependency(
                value = "!org.jetbrains.kotlin:kotlin-script-runtime:" + CoreEnv.KOTLIN_VERSION,
                transitive = false
        ),
        @RuntimeDependency(
                value = "!org.jetbrains.kotlin:kotlin-scripting-common:" + CoreEnv.KOTLIN_VERSION,
                transitive = false
        ),
        @RuntimeDependency(
                value = "!org.jetbrains.kotlin:kotlin-scripting-jvm:" + CoreEnv.KOTLIN_VERSION,
                transitive = false
        ),
        @RuntimeDependency(
                value = "!org.jetbrains.kotlin:kotlin-scripting-jvm-host-unshaded:" + CoreEnv.KOTLIN_VERSION,
                transitive = false
        ),
        @RuntimeDependency(
                value = "!org.jetbrains.kotlin:kotlin-scripting-compiler:" + CoreEnv.KOTLIN_VERSION,
                transitive = false
        ),
        @RuntimeDependency(
                value = "!org.jetbrains.kotlin:kotlin-scripting-compiler-impl:" + CoreEnv.KOTLIN_VERSION,
                transitive = false
        ),
        @RuntimeDependency(
                value = "!org.jetbrains.kotlin:kotlin-scripting-jsr223-unshaded:" + CoreEnv.KOTLIN_VERSION,
                transitive = false
        ),
        @RuntimeDependency(
                value = "!org.jetbrains.intellij.deps:trove4j:1.0.20200330",
                test = "!gnu.trove.TObjectHashingStrategy",
                transitive = false
        ),
        // JavaScript
        @RuntimeDependency(
                value = "!org.openjdk.nashorn:nashorn-core:15.4",
                test = "!org.openjdk.nashorn.api.scripting.NashornScriptEngine"
        ),
        // Jexl
        @RuntimeDependency(
                value = "!org.apache.commons:commons-jexl3:3.4.0",
                test = "!org.apache.commons.jexl3.JexlEngine",
                transitive = false
        )
})
public class ScriptEnv {
}
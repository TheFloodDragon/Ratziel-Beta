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
                value = "!org.jetbrains.kotlin:kotlin-script-runtime:" + CoreEnv.KOTLIN_VERSION,
                transitive = false
        ),
        @RuntimeDependency(
                value = "!org.jetbrains.kotlin:kotlin-script-common:" + CoreEnv.KOTLIN_VERSION,
                transitive = false
        ),
        @RuntimeDependency(
                value = "!org.jetbrains.kotlin:kotlin-script-jvm:" + CoreEnv.KOTLIN_VERSION,
                transitive = false
        ),
        @RuntimeDependency(
                value = "!org.jetbrains.kotlin:kotlin-script-jvm-host:" + CoreEnv.KOTLIN_VERSION,
                transitive = false
        ),
        @RuntimeDependency(
                value = "!org.jetbrains.kotlin:kotlin-script-compiler:" + CoreEnv.KOTLIN_VERSION,
                transitive = false
        ),
        @RuntimeDependency(
                value = "!org.jetbrains.kotlin:kotlin-script-jsr233:" + CoreEnv.KOTLIN_VERSION,
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
                test = "!org.apache.commons.jexl3.JexlEngine"
        )}
)
public class ScriptEnv {
}
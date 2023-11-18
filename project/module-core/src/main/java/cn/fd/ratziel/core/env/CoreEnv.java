package cn.fd.ratziel.core.env;

import taboolib.common.env.RuntimeDependency;

import static cn.fd.ratziel.core.env.CoreEnv.kotlin_version_escaped;

/**
 * CoreEnv
 *
 * @author TheFloodDragon
 * @since 2023/8/23 16:20
 */
@RuntimeDependency(
        value = "!org.jetbrains.kotlinx:kotlinx-serialization-core-jvm:1.6.1",
        test = "!kotlinx" + kotlin_version_escaped + ".serialization.Serializer",
        relocate = {"!kotlin.", "!kotlin" + kotlin_version_escaped + ".", "!kotlinx.", "!kotlinx" + kotlin_version_escaped + "."},
        transitive = false
)
@RuntimeDependency(
        value = "!org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.6.1",
        test = "!kotlinx" + kotlin_version_escaped + ".serialization.json.JsonKt",
        relocate = {"!kotlin.", "!kotlin" + kotlin_version_escaped + ".", "!kotlinx.", "!kotlinx" + kotlin_version_escaped + "."},
        transitive = false
)
@RuntimeDependency(
        value = "!org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.7.3",
        relocate = {"!kotlin.", "!kotlin" + kotlin_version_escaped + ".", "!kotlinx.", "!kotlinx" + kotlin_version_escaped + "."},
        transitive = false
)

public class CoreEnv {
    public static final String kotlin_version_escaped = "1920";
}
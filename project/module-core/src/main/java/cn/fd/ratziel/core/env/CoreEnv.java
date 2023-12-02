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
        value = "!org.jetbrains.kotlinx:kotlinx-serialization-core-jvm:" + CoreEnv.serialization_version,
        test = "!kotlinx" + kotlin_version_escaped + ".serialization.Serializer",
        relocate = {"!kotlin.", "!kotlin" + kotlin_version_escaped + ".", "!kotlinx.", "!kotlinx" + kotlin_version_escaped + "."},
        transitive = false
)
@RuntimeDependency(
        value = "!org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:" + CoreEnv.serialization_version,
        test = "!kotlinx" + kotlin_version_escaped + ".serialization.json.JsonKt",
        relocate = {"!kotlin.", "!kotlin" + kotlin_version_escaped + ".", "!kotlinx.", "!kotlinx" + kotlin_version_escaped + "."},
        transitive = false
)
@RuntimeDependency(
        value = "!org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:" + CoreEnv.coroutines_version,
        relocate = {"!kotlin.", "!kotlin" + kotlin_version_escaped + ".", "!kotlinx.", "!kotlinx" + kotlin_version_escaped + "."},
        transitive = false
)

public class CoreEnv {
    public static final String kotlin_version_escaped = "1921";
    public static final String serialization_version = "1.6.2";
    public static final String coroutines_version = "1.7.3";
}
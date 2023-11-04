package cn.fd.ratziel.core.env;

import taboolib.common.env.RuntimeDependency;

/**
 * CoreEnv
 *
 * @author TheFloodDragon
 * @since 2023/8/23 16:20
 */
@RuntimeDependency(
        value = "!org.jetbrains.kotlinx:kotlinx-serialization-core-jvm:1.6.0",
        test = "!kotlinx@kotlin_version_escape@.serialization.Serializer",
        relocate = {"!kotlin.", "!kotlin@kotlin_version_escape@.", "!kotlinx.", "!kotlinx@kotlin_version_escape@."},
        transitive = false
)
@RuntimeDependency(
        value = "!org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.6.0",
        test = "!kotlinx1920.serialization.json.JsonKt",
        relocate = {"!kotlin.", "!kotlin@kotlin_version_escape@.", "!kotlinx.", "!kotlinx@kotlin_version_escape@."},
        transitive = false
)
@RuntimeDependency(
        value = "!org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.7.3",
        relocate = {"!kotlin.", "!kotlin@kotlin_version_escape@.", "!kotlinx.", "!kotlinx@kotlin_version_escape@."},
        transitive = false
)

public class CoreEnv {
}
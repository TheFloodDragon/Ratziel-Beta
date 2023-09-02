package cn.fd.ratziel.env;

import taboolib.common.env.RuntimeDependency;

/**
 * CoreEnv
 *
 * @author TheFloodDragon
 * @since 2023/8/23 16:20
 */
@RuntimeDependency(
        value = "!org.jetbrains.kotlinx:kotlinx-serialization-core-jvm:1.6.0",
        test = "!kotlinx1910.serialization.Serializer",
        relocate = {"!kotlin.", "!kotlin1910.", "!kotlinx.", "!kotlinx1910."},
        transitive = false
)
@RuntimeDependency(
        value = "!org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.6.0",
        test = "!kotlinx1910.serialization.json.JsonKt",
        relocate = {"!kotlin.", "!kotlin1910.", "!kotlinx.", "!kotlinx1910."},
        transitive = false
)
@RuntimeDependency(
        value = "!org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.7.3",
        relocate = {"!kotlin.", "!kotlin1910.", "!kotlinx.", "!kotlinx1910."},
        transitive = false
)

public class CoreEnv {
}

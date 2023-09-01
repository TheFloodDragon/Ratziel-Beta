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
        test = "!kotlinx190_160.serialization.Serializer",
        relocate = {"!kotlin.", "!kotlin190.", "!kotlinx.", "!kotlinx190_160."},
        transitive = false
)
@RuntimeDependency(
        value = "!org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.6.0",
        test = "!kotlinx190_160.serialization.json.JsonKt",
        relocate = {"!kotlin.", "!kotlin190.", "!kotlinx.", "!kotlinx190_160."},
        transitive = false
)
@RuntimeDependency(
        value = "!org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.7.3",
        relocate = {"!kotlin.", "!kotlin190.", "!kotlinx.", "!kotlinx190_173."},
        transitive = false
)

public class CoreEnv { }

package cn.fd.ratziel.env;

import taboolib.common.env.RuntimeDependency;

/**
 * CoreEnv
 *
 * @author TheFloodDragon
 * @since 2023/8/23 16:20
 */
@RuntimeDependency(
        value = "!org.jetbrains.kotlinx:kotlinx-serialization-core-jvm:@kotlin_serialization_version@",
        test = "!kotlinx1910.serialization.Serializer",
        relocate = {"!kotlin.", "!kotlin@kotlin_escaped_version@.", "!kotlinx.", "!kotlinx@kotlin_escaped_version@."},
        transitive = false
)
@RuntimeDependency(
        value = "!org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:@kotlin_serialization_version@",
        test = "!kotlinx@kotlin_escaped_version@.serialization.json.JsonKt",
        relocate = {"!kotlin.", "!kotlin@kotlin_escaped_version@.", "!kotlinx.", "!kotlinx@kotlin_escaped_version@."},
        transitive = false
)
@RuntimeDependency(
        value = "!org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:@kotlin_coroutine_version@",
        relocate = {"!kotlin.", "!kotlin@kotlin_escaped_version@.", "!kotlinx.", "!kotlinx@kotlin_escaped_version@."},
        transitive = false
)

public class CoreEnv { }
package cn.fd.ratziel.core.env;

import taboolib.common.env.RuntimeDependency;

/**
 * CoreEnv
 * Since Taboolib 6.1, Kotlin Coroutines is used by default
 *
 * @author TheFloodDragon
 * @since 2023/8/23 16:20
 */
@RuntimeDependency(
        value = "!org.jetbrains.kotlinx:kotlinx-serialization-core-jvm:" + CoreEnv.KOTLIN_SERIALIZATION_VERSION,
        relocate = {CoreEnv.KOTLIN_SERIALIZATION_ID + ".", CoreEnv.KOTLIN_SERIALIZATION_ID + CoreEnv.KOTLIN_SERIALIZATION_VERSION_ESCAPED + "."},
        transitive = false
)
@RuntimeDependency(
        value = "!org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:" + CoreEnv.KOTLIN_SERIALIZATION_VERSION,
        relocate = {CoreEnv.KOTLIN_SERIALIZATION_ID + ".", CoreEnv.KOTLIN_SERIALIZATION_ID + CoreEnv.KOTLIN_SERIALIZATION_VERSION_ESCAPED + "."},
        transitive = false
)
public class CoreEnv {

    public static final String KOTLIN_SERIALIZATION_VERSION = "1.6.3";
    public static final String KOTLIN_SERIALIZATION_VERSION_ESCAPED = "163";
    public static final String KOTLIN_SERIALIZATION_ID = "!kotlinx.serialization";

}
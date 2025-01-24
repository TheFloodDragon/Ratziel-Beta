package cn.fd.ratziel.core.env;

import taboolib.common.env.RuntimeDependencies;
import taboolib.common.env.RuntimeDependency;

/**
 * CoreEnv
 *
 * @author TheFloodDragon
 * @since 2023/8/23 16:20
 */
@RuntimeDependencies({
        @RuntimeDependency(value = "!org.jetbrains.kotlinx:kotlinx-serialization-core-jvm:1.8.0", transitive = false),
        @RuntimeDependency(value = "!org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.8.0", transitive = false)
})
public class CoreEnv {
    public static final String KOTLIN_VERSION = "2.1.0";
}

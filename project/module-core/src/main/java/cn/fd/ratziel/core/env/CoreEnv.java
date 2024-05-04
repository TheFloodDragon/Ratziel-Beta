package cn.fd.ratziel.core.env;

import taboolib.common.env.RuntimeDependencies;
import taboolib.common.env.RuntimeDependency;

/**
 * CoreEnv Since Taboolib 6.1, Kotlin Coroutines is used by default
 *
 * @author TheFloodDragon
 * @since 2023/8/23 16:20
 */
@RuntimeDependencies({
    @RuntimeDependency(value = "org.jetbrains.kotlinx:kotlinx-serialization-core-jvm:1.6.3", test = "!kotlinx.serialization.KSerializer", transitive = false),
    @RuntimeDependency(value = "org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.6.3", test = "!kotlinx.serialization.json.JsonElement", transitive = false)
})
public class CoreEnv {
}

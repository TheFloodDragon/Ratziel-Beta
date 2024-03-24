package cn.fd.ratziel.core.env;

import taboolib.common.env.RuntimeDependency;

/**
 * CoreEnv
 * Since Taboolib 6.1, Kotlin Coroutines is used by default
 *
 * @author TheFloodDragon
 * @since 2023/8/23 16:20
 */
@RuntimeDependency("org.jetbrains.kotlinx:kotlinx-serialization-core-jvm:1.6.3")
@RuntimeDependency("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.6.3")
public class CoreEnv {
}
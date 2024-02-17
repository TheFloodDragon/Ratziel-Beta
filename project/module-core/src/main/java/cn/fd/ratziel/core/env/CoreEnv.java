package cn.fd.ratziel.core.env;

import taboolib.common.env.JarRelocation;
import taboolib.common.env.RuntimeEnv;
import taboolib.common.platform.Awake;

import java.util.Arrays;

import static taboolib.common.PrimitiveSettings.KOTLIN_VERSION;
import static taboolib.common.PrimitiveSettings.formatVersion;
import static taboolib.common.env.RuntimeEnv.KOTLIN_ID;

/**
 * CoreEnv
 * Since Taboolib 6.1, Kotlin Coroutines is used by default
 *
 * @author TheFloodDragon
 * @since 2023/8/23 16:20
 */
public class CoreEnv {

    public static final String KOTLIN_SERIALIZATION_VERSION = "1.6.3";

    public static final String KOTLIN_SERIALIZATION_ID = "!kotlinx.serialization".substring(1);

    public static final JarRelocation KOTLIN_RELOCATION = new JarRelocation(KOTLIN_ID + ".", KOTLIN_ID + formatVersion(KOTLIN_VERSION) + ".");
    public static final JarRelocation KOTLIN_SERIALIZATION_RELOCATION = new JarRelocation(KOTLIN_SERIALIZATION_ID + ".", KOTLIN_SERIALIZATION_ID + formatVersion(KOTLIN_SERIALIZATION_VERSION) + ".");

    public static final String[] RUNTIME_DEPENDENCIES = {
            "!org.jetbrains.kotlinx:kotlinx-serialization-core-jvm:".substring(1) + KOTLIN_SERIALIZATION_VERSION,
            "!org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:".substring(1) + KOTLIN_SERIALIZATION_VERSION,
    };

    @Awake
    public static void init() throws Throwable {
        for (String dependency : RUNTIME_DEPENDENCIES) {
            RuntimeEnv.ENV.loadDependency(dependency, Arrays.asList(KOTLIN_RELOCATION, KOTLIN_SERIALIZATION_RELOCATION));
        }
    }

}
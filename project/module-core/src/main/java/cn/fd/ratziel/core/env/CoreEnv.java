package cn.fd.ratziel.core.env;

import taboolib.common.env.JarRelocation;
import taboolib.common.env.RuntimeEnv;
import taboolib.common.platform.Awake;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static taboolib.common.PrimitiveSettings.formatVersion;
import static taboolib.common.PrimitiveSettings.KOTLIN_VERSION;
import static taboolib.common.env.RuntimeEnv.KOTLIN_ID;

/**
 * CoreEnv
 * Since Taboolib 6.1, Kotlin Coroutines is used by default
 *
 * @author TheFloodDragon
 * @since 2023/8/23 16:20
 */
public class CoreEnv {

    public static final String KOTLIN_SERIALIZATION_VERSION = "1.6.2";

    public static final String KOTLIN_SERIALIZATION_ID = "!kotlinx.serialization".substring(1);

    public static final String[] KOTLIN_RELOCATION = {KOTLIN_ID + ".", KOTLIN_ID + formatVersion(KOTLIN_VERSION) + "."};
    public static final String[] KOTLIN_SERIALIZATION_RELOCATION = {KOTLIN_SERIALIZATION_ID + ".", KOTLIN_SERIALIZATION_ID + formatVersion(KOTLIN_SERIALIZATION_ID) + "."};

    public static final String[] RUNTIME_DEPENDENCIES = {
            "!org.jetbrains.kotlinx:kotlinx-serialization-core-jvm:" + KOTLIN_SERIALIZATION_VERSION,
            "!org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:" + KOTLIN_SERIALIZATION_VERSION,
    };

    @Awake
    public static void init() throws Throwable {
        // Relocations
        List<JarRelocation> relocations = Arrays.stream(new String[][]{KOTLIN_RELOCATION, KOTLIN_SERIALIZATION_RELOCATION})
                .map(relocation -> new JarRelocation(relocation[0], relocation[1])).collect(Collectors.toList());
        // Load as dependencies
        for (String dependency : RUNTIME_DEPENDENCIES) {
            RuntimeEnv.ENV.loadDependency(dependency, relocations);
        }
    }

}
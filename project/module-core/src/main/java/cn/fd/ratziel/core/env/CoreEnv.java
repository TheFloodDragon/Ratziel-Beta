package cn.fd.ratziel.core.env;

import taboolib.common.env.JarRelocation;
import taboolib.common.env.RuntimeEnv;
import taboolib.common.platform.Awake;

import java.util.Arrays;

import static taboolib.common.PrimitiveSettings.KOTLIN_VERSION;

/**
 * CoreEnv
 * Since Taboolib 6.1, Kotlin Coroutines is used by default
 *
 * @author TheFloodDragon
 * @since 2023/8/23 16:20
 */
public class CoreEnv {

    public static final String KOTLIN_SERIALIZATION_VERSION = "1.6.2";

    private static final String kt = "!kotlin".substring(1);
    private static final String kts = "!kotlinx.serialization".substring(1);
    private static final String kv = KOTLIN_VERSION.replace(".", "");
    private static final String kvs = KOTLIN_SERIALIZATION_VERSION.replace(".", "");

    public static final JarRelocation KOTLIN_RELOCATION = new JarRelocation(kt + ".", kt + kv + ".");
    public static final JarRelocation KOTLIN_SERIALIZATION_RELOCATION = new JarRelocation(kts + ".", kts + kvs + ".");

    public static final String[] RUNTIME_DEPENDENCIES = {
            "!org.jetbrains.kotlinx:kotlinx-serialization-core-jvm:" + KOTLIN_SERIALIZATION_VERSION,
            "!org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:" + KOTLIN_SERIALIZATION_VERSION,
    };

    @Awake
    public static void init() throws Throwable {
        for (String dependency : RUNTIME_DEPENDENCIES) {
            RuntimeEnv.ENV.loadDependency(dependency, false, Arrays.asList(KOTLIN_RELOCATION, KOTLIN_SERIALIZATION_RELOCATION));
        }
    }

}
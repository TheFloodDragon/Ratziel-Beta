package cn.fd.ratziel.core.env;

import taboolib.common.env.RuntimeEnv;
import taboolib.common.platform.Awake;

import java.util.Collections;

/**
 * CommonEnv
 *
 * @author TheFloodDragon
 * @since 2023/5/21 11:06
 */
public class CommonEnv {

    public static final String ADVENTURE_VERSION = "4.15.0";

    public static final String[] RUNTIME_DEPENDENCIES = {
            "!net.kyori:adventure-api:".substring(1) + ADVENTURE_VERSION,
            "!net.kyori:adventure-text-minimessage:".substring(1) + ADVENTURE_VERSION,
    };

    @Awake
    public static void init() throws Throwable {
        for (String dependency : RUNTIME_DEPENDENCIES) {
            RuntimeEnv.ENV.loadDependency(dependency, true, Collections.emptyList());
        }
    }

}
package cn.fd.ratziel.core.env;

import taboolib.common.env.RuntimeEnv;
import taboolib.common.platform.Awake;

import java.util.Collections;

/**
 * BukkitEnv
 *
 * @author TheFloodDragon
 * @since 2023/5/21 10:58
 */
public class BukkitEnv {

    public static final String ADVENTURE_PLATFORM_VERSION = "4.3.2";

    public static final String RUNTIME_DEPENDENCY = "!net.kyori:adventure-platform-bukkit:".substring(1) + ADVENTURE_PLATFORM_VERSION;

    @Awake
    public static void init() throws Throwable {
        RuntimeEnv.ENV.loadDependency(RUNTIME_DEPENDENCY, true, Collections.emptyList());
    }

}
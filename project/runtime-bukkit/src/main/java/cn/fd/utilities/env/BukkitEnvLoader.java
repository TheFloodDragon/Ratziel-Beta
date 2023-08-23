package cn.fd.utilities.env;

import taboolib.common.env.RuntimeEnv;
import taboolib.common.platform.Awake;

/**
 * BukkitEnvLoader
 *
 * @author TheFloodDragon
 * @since 2023/8/23 16:29
 */
public class BukkitEnvLoader {
    @Awake
    private void loadDependency() {
        RuntimeEnv.ENV.loadDependency(BukkitEnv.class, true);
    }
}

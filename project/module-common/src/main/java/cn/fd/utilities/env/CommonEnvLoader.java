package cn.fd.utilities.env;

import taboolib.common.env.RuntimeEnv;
import taboolib.common.platform.Awake;

/**
 * CommonEnvLoader
 *
 * @author TheFloodDragon
 * @since 2023/8/23 16:29
 */
public class CommonEnvLoader {
    @Awake
    private void loadDependency() {
        RuntimeEnv.ENV.loadDependency(CommonEnv.class, true);
    }
}
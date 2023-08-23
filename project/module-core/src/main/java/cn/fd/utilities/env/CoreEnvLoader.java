package cn.fd.utilities.env;

import taboolib.common.env.RuntimeEnv;
import taboolib.common.platform.Awake;

/**
 * CoreEnvLoader
 *
 * @author TheFloodDragon
 * @since 2023/8/23 16:27
 */
public class CoreEnvLoader {

    @Awake
    private void loadDependency() {
        RuntimeEnv.ENV.loadDependency(CoreEnv.class, true);
    }

}

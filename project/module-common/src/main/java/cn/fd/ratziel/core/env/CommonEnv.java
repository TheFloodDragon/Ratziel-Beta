package cn.fd.ratziel.core.env;

import taboolib.common.env.RuntimeDependency;

/**
 * CommonEnv
 *
 * @author TheFloodDragon
 * @since 2023/5/21 11:06
 */
@RuntimeDependency("!net.kyori:adventure-api:" + CommonEnv.ADVENTURE_VERSION)
@RuntimeDependency("!net.kyori:adventure-text-minimessage:" + CommonEnv.ADVENTURE_VERSION)
public class CommonEnv {

    public static final String ADVENTURE_VERSION = "4.15.0";

    public static final String ADVENTURE_PLATFORM_VERSION = "4.3.2";

}
package cn.fd.ratziel.core.env;

import taboolib.common.env.RuntimeDependency;

/**
 * CommonEnv
 *
 * @author TheFloodDragon
 * @since 2023/5/21 11:06
 */
@RuntimeDependency(
        value = "!net.kyori:adventure-api:" + CommonEnv.version,
        test = "net.kyori.adventure.Adventure"
)

@RuntimeDependency(
        value = "!net.kyori:adventure-text-minimessage:" + CommonEnv.version
)
public class CommonEnv {
    public static final String version = "4.15.0";
}
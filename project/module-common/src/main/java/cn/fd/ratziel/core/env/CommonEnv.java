package cn.fd.ratziel.core.env;

import taboolib.common.env.RuntimeDependency;

/**
 * CommonEnv
 *
 * @author TheFloodDragon
 * @since 2023/5/21 11:06
 */
@RuntimeDependency(
        value = "!net.kyori:adventure-api:4.14.0",
        initiative = true,
        transitive = false
)
@RuntimeDependency(
        value = "!net.kyori:adventure-text-minimessage:4.14.0",
        transitive = false
)

public class CommonEnv {
}
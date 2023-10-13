package cn.fd.ratziel.env;

import taboolib.common.env.RuntimeDependency;

/**
 * CommonEnv
 *
 * @author TheFloodDragon
 * @since 2023/5/21 11:06
 */
@RuntimeDependency(
        value = "!net.kyori:adventure-api:@adventure_api_version@",
        initiative = true,
        transitive = false
)
@RuntimeDependency(
        value = "!net.kyori:adventure-text-minimessage:@adventure_api_version@",
        transitive = false
)

public class CommonEnv { }
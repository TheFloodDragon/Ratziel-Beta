package cn.fd.ratziel.core.env;

import taboolib.common.env.RuntimeDependency;
import taboolib.common.env.RuntimeEnv;
import taboolib.common.platform.Awake;

import java.util.Collections;

/**
 * BukkitEnv
 *
 * @author TheFloodDragon
 * @since 2023/5/21 10:58
 */
@RuntimeDependency("!net.kyori:adventure-platform-bukkit:" + CommonEnv.ADVENTURE_PLATFORM_VERSION)
public class BukkitEnv {
}
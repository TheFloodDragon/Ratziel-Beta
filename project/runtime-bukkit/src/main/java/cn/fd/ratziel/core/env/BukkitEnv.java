package cn.fd.ratziel.core.env;

import taboolib.common.env.RuntimeDependency;
import taboolib.common.platform.Platform;
import taboolib.common.platform.PlatformSide;

/**
 * BukkitEnv
 *
 * @author TheFloodDragon
 * @since 2023/5/21 10:58
 */
@PlatformSide(Platform.BUKKIT)
@RuntimeDependency(
        value = "!net.kyori:adventure-platform-bukkit:" + AdventureEnv.ADVENTURE_PLATFORM_VERSION,
        test = "!net.kyori.adventure.platform.bukkit.BukkitAudience"
)
public class BukkitEnv {
}

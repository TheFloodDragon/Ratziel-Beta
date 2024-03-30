package cn.fd.ratziel.core.env;

import taboolib.common.env.RuntimeDependency;

/**
 * BukkitEnv
 *
 * @author TheFloodDragon
 * @since 2023/5/21 10:58
 */
@RuntimeDependency(value = "!net.kyori:adventure-platform-bukkit:4.3.2", test = "!net.kyori.adventure.platform.bukkit.BukkitAudience")
public class BukkitEnv {
}
package cn.fd.fdutilities;

import taboolib.common.env.RuntimeDependency;

/**
 * @author MC~蛟龙
 * @since 2022/6/30 13:26
 * 自动安装依赖
 */
@RuntimeDependency(
        value = "!net.kyori:adventure-api:4.11.0",
        test = "!net.kyori.adventure.Adventure",
        initiative = true
)
@RuntimeDependency(
        value = "!net.kyori:adventure-platform-bukkit:4.1.1",
        test = "!net.kyori.adventure.platform.bukkit.BukkitAudiences",
        repository = "https://repo.maven.apache.org/maven2",
        initiative = true
)
@RuntimeDependency(
        value = "!net.kyori:adventure-text-minimessage:4.11.0",
        repository = "https://repo.maven.apache.org/maven2",
        //repository = "https://s01.oss.sonatype.org/content/repositories/snapshots",
        initiative = true
)

public class BukkitEnv {
}
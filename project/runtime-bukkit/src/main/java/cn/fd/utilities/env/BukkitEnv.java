package cn.fd.utilities.env;

import taboolib.common.env.RuntimeDependency;

/**
 * @author MC~蛟龙
 * @since 2023/5/21 10:58
 * 自动安装依赖
 */
@RuntimeDependency(
        value = "!net.kyori:adventure-platform-bukkit:4.3.0",
        test = "!net.kyori.adventure.platform.bukkit.BukkitAudiences",
        //repository = "https://repo.maven.apache.org/maven2",
        initiative = true
)

public class BukkitEnv {
}

package cn.fd.utilities.env;

import taboolib.common.env.RuntimeDependency;

/**
 * @author MC~蛟龙
 * @since 2023/5/21 11:06
 * 自动安装依赖
 */
@RuntimeDependency(
        value = "!net.kyori:adventure-api:4.3.0",
        test = "!net.kyori.adventure.Adventure",
        initiative = true
)
@RuntimeDependency(
        value = "!net.kyori:adventure-text-minimessage:4.13.1",
        //repository = "https://repo.maven.apache.org/maven2",
        //repository = "https://s01.oss.sonatype.org/content/repositories/snapshots",
        initiative = true
)

public class CommonEnv {
}
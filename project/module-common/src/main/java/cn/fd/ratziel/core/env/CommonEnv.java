package cn.fd.ratziel.core.env;

import taboolib.common.env.RuntimeDependency;

/**
 * CommonEnv
 *
 * @author TheFloodDragon
 * @since 2023/5/21 11:06
 */
@RuntimeDependency(
        value = "!net.kyori:adventure-api:4.16.0",
        test = "!net.kyori.adventure.text.Component"
)
@RuntimeDependency(
        value = "!net.kyori:text-serializer-gson:4.16.0",
        test = "!net.kyori.adventure.text.serializer.gson.GsonComponentSerializer"
)
@RuntimeDependency(
        value = "!net.kyori:adventure-text-minimessage:4.16.0",
        test = "!net.kyori.adventure.text.minimessage.MiniMessage"
)
@RuntimeDependency(
        value = "!net.kyori:text-serializer-legacy:4.16.0",
        test = "!net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer"
)
public class CommonEnv {
}
package cn.fd.ratziel.core.env;

import taboolib.common.env.RuntimeDependencies;
import taboolib.common.env.RuntimeDependency;

/**
 * CommonEnv
 *
 * @author TheFloodDragon
 * @since 2023/5/21 11:06
 */
@RuntimeDependencies({
    @RuntimeDependency(
            value = "!net.kyori:adventure-api:" + CommonEnv.ADVENTURE_VERSION,
            test = "!net.kyori.adventure.text.Component"
    ),
    @RuntimeDependency(
            value = "!net.kyori:adventure-text-serializer-gson:" + CommonEnv.ADVENTURE_VERSION,
            test = "!net.kyori.adventure.text.serializer.gson.GsonComponentSerializer",
            transitive = false
    ),
    @RuntimeDependency(
            value = "!net.kyori:adventure-text-minimessage:" + CommonEnv.ADVENTURE_VERSION,
            test = "!net.kyori.adventure.text.minimessage.MiniMessage",
            transitive = false
    ),
    @RuntimeDependency(
            value = "!net.kyori:adventure-text-serializer-legacy:" + CommonEnv.ADVENTURE_VERSION,
            test = "!net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer",
            transitive = false
    ),
    @RuntimeDependency(
            value = "!net.kyori:adventure-platform-api:" + CommonEnv.ADVENTURE_PLATFORM_VERSION,
            test = "!net.kyori.adventure.platform.AudienceProvider",
            transitive = false
    )
})
public class CommonEnv {

    public final static String ADVENTURE_VERSION = "4.16.0";
    public final static String ADVENTURE_PLATFORM_VERSION = "4.3.2";

}

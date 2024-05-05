package cn.fd.ratziel.core.env;

import taboolib.common.env.RuntimeDependencies;
import taboolib.common.env.RuntimeDependency;

/**
 * AdventureEnv
 *
 * @author TheFloodDragon
 * @since 2024/5/5 8:09
 */
@RuntimeDependencies({
        @RuntimeDependency(
                value = "!net.kyori:adventure-api:" + AdventureEnv.ADVENTURE_VERSION,
                test = "!net.kyori.adventure.text.Component"
        ),
        @RuntimeDependency(
                value = "!net.kyori:adventure-text-serializer-gson:" + AdventureEnv.ADVENTURE_VERSION,
                test = "!net.kyori.adventure.text.serializer.gson.GsonComponentSerializer"
        ),
        @RuntimeDependency(
                value = "!net.kyori:adventure-text-minimessage:" + AdventureEnv.ADVENTURE_VERSION,
                test = "!net.kyori.adventure.text.minimessage.MiniMessage"
        ),
        @RuntimeDependency(
                value = "!net.kyori:adventure-text-serializer-legacy:" + AdventureEnv.ADVENTURE_VERSION,
                test = "!net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer"
        ),
        @RuntimeDependency(
                value = "!net.kyori:adventure-platform-api:" + AdventureEnv.ADVENTURE_PLATFORM_VERSION,
                test = "!net.kyori.adventure.platform.AudienceProvider"
        )
})
public class AdventureEnv {

    public final static String ADVENTURE_VERSION = "4.16.0";
    public final static String ADVENTURE_PLATFORM_VERSION = "4.3.2";

}

package cn.fd.ratziel.core.env;

import taboolib.common.env.RuntimeDependencies;
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
@RuntimeDependencies({
    @RuntimeDependency(
            value = "!net.kyori:adventure-platform-bukkit:" + AdventureEnv.ADVENTURE_PLATFORM_VERSION,
            test = "!net.kyori.adventure.platform.bukkit.BukkitAudience",
            transitive = false
    ),
    @RuntimeDependency(
            value = "!net.kyori:adventure-text-serializer-bungeecord:" + AdventureEnv.ADVENTURE_PLATFORM_VERSION,
            test = "!net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer",
            transitive = false
    ),
    @RuntimeDependency(
            value = "!net.kyori:adventure-nbt:" + AdventureEnv.ADVENTURE_VERSION,
            test = "!net.kyori.adventure.nbt.BinaryTag"
    ),
    @RuntimeDependency(
            value = "!net.kyori:adventure-platform-facet:" + AdventureEnv.ADVENTURE_PLATFORM_VERSION,
            test = "!net.kyori.adventure.platform.facet.FacetAudience",
            transitive = false
    ),
    @RuntimeDependency(
            value = "!net.kyori:adventure-platform-viaversion:" + AdventureEnv.ADVENTURE_PLATFORM_VERSION,
            test = "!net.kyori.adventure.platform.viaversion.ViaFacet",
            transitive = false
    ),
    @RuntimeDependency(
            value = "!net.kyori:adventure-text-serializer-json:" + AdventureEnv.ADVENTURE_VERSION,
            test = "!net.kyori.adventure.text.serializer.json.JSONComponentSerializer",
            transitive = false
    ),
    @RuntimeDependency(
            value = "!net.kyori:adventure-text-serializer-json-legacy-impl:" + AdventureEnv.ADVENTURE_VERSION,
            test = "!net.kyori.adventure.text.serializer.json.legacyimpl.NBTLegacyHoverEventSerializer",
            transitive = false
    ),
    @RuntimeDependency(
            value = "!net.kyori:adventure-text-serializer-gson-legacy-impl:" + AdventureEnv.ADVENTURE_VERSION,
            test = "!net.kyori.adventure.text.serializer.gson.legacyimpl.NBTLegacyHoverEventSerializer",
            transitive = false
    )
})
public class BukkitEnv {
}

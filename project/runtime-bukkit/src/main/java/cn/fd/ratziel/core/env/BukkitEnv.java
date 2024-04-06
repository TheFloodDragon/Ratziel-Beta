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
        value = "!net.kyori:adventure-platform-bukkit:" + CommonEnv.ADVENTURE_PLATFORM_VERSION,
        test = "!net.kyori.adventure.platform.bukkit.BukkitAudience",
        transitive = false
)
@RuntimeDependency(
        value = "!net.kyori:adventure-text-serializer-bungeecord:" + CommonEnv.ADVENTURE_PLATFORM_VERSION,
        test = "!net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer",
        transitive = false
)
@RuntimeDependency(
        value = "!net.kyori:adventure-text-serializer-bungeecord:" + CommonEnv.ADVENTURE_PLATFORM_VERSION,
        test = "!net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer",
        transitive = false
)
@RuntimeDependency(
        value = "!net.kyori:adventure-nbt:" + CommonEnv.ADVENTURE_VERSION,
        test = "!net.kyori.adventure.nbt.BinaryTag",
        transitive = false
)
@RuntimeDependency(
        value = "!net.kyori:adventure-platform-facet:" + CommonEnv.ADVENTURE_PLATFORM_VERSION,
        test = "!net.kyori.adventure.platform.facet.FacetAudience",
        transitive = false
)
@RuntimeDependency(
        value = "!net.kyori:adventure-platform-viaversion:" + CommonEnv.ADVENTURE_PLATFORM_VERSION,
        test = "!net.kyori.adventure.platform.viaversion.ViaFacet",
        transitive = false
)
@RuntimeDependency(
        value = "!net.kyori:adventure-text-serializer-gson-legacy-impl:" + CommonEnv.ADVENTURE_VERSION,
        test = "!net.kyori.adventure.text.serializer.gson.legacyimpl.NBTLegacyHoverEventSerializer",
        transitive = false
)
public class BukkitEnv {
}
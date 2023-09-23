package cn.fd.ratziel.folia

import cn.fd.ratziel.core.util.hasClass

/**
 * ServerPlatform
 *
 * @author TheFloodDragon
 * @since 2023/9/23 11:22
 */
enum class ServerPlatform(val isRunWith: Boolean, val priority: Byte) {

    FOLIA(
        hasClass("io.papermc.paper.threadedregions.RegionizedServerInitEvent")
                || hasClass("io.papermc.paper.threadedregions.RegionizedServer"), 4
    ),
    PURPUR(
        hasClass("org.purpurmc.purpur.language.Language"),
        3
    ),
    PAPER(
        hasClass("com.destroystokyo.paper.PaperConfig") || hasClass("io.papermc.paper.configuration.Configuration"),
        2
    ),
    SPIGOT(hasClass("org.spigotmc.SpigotConfig"), 1),
    BUKKIT(true, 0);

    companion object {
        /**
         * 获取运行平台
         */
        fun infer() =
            entries.sortedByDescending { it.priority } // 从大到小排列
                .find { it.isRunWith } ?: BUKKIT
    }

}
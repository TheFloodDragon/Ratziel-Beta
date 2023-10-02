package cn.fd.ratziel.item

import cn.fd.ratziel.adventure.serializeByMiniMessage
import cn.fd.ratziel.adventure.toJsonFormat
import net.kyori.adventure.text.Component
import org.bukkit.inventory.meta.ItemMeta
import taboolib.library.reflex.Reflex.Companion.setProperty
import taboolib.module.nms.MinecraftVersion

fun ItemMeta.setDisplayName(component: Component) = this.apply {
    if (MinecraftVersion.isLower(MinecraftVersion.V1_17)) {
        setDisplayName(serializeByMiniMessage(component))
    } else setProperty("displayName", component.toJsonFormat())
}

fun ItemMeta.setLore(components: Iterable<Component>) = this.apply {
    if (MinecraftVersion.isLower(MinecraftVersion.V1_17)) {
        lore = components.map { serializeByMiniMessage(it) }
    } else setProperty("lore", components.map { it.toJsonFormat() })
}
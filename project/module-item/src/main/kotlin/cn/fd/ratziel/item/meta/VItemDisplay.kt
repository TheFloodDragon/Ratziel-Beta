@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.item.meta

import cn.fd.ratziel.common.adventure.buildMessageMJ
import cn.fd.ratziel.item.api.builder.ItemMetaBuilder
import cn.fd.ratziel.item.api.meta.ItemDisplay
import cn.fd.ratziel.item.util.nmsComponent
import kotlinx.serialization.Contextual
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import net.kyori.adventure.text.Component
import org.bukkit.inventory.meta.ItemMeta
import taboolib.library.reflex.Reflex.Companion.setProperty

/**
 * VItemDisplay
 *
 * @author TheFloodDragon
 * @since 2023/10/14 16:15
 */
@Serializable
data class VItemDisplay(
    @JsonNames("name", "display-name", "displayName")
    override var name: @Contextual Component? = null,
    @JsonNames("loc-name", "locName", "local-name", "localName")
    override var localizedName: @Contextual Component? = null,
    @JsonNames("lores")
    override var lore: @Contextual List<Component>? = emptyList(),
) : ItemDisplay, ItemMetaBuilder {

    /**
     * 设置显示名称
     */
    fun setName(origin: String?) {
        name = buildMessageMJ(origin)
    }

    /**
     * 设置描述
     */
    fun setLore(components: Iterable<String?>) {
        lore = components.map { buildMessageMJ(it) }
    }

    override fun build(meta: ItemMeta) {
        meta.setProperty("displayName", nmsComponent(name))
        meta.setProperty("lore", lore?.mapNotNull { nmsComponent(it) })
        meta.setProperty("locName", nmsComponent(localizedName))
    }

}
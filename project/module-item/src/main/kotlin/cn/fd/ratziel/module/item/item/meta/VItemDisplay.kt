@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.module.item.item.meta

import cn.fd.ratziel.common.adventure.buildMessageMJ
import cn.fd.ratziel.common.adventure.toJsonFormat
import cn.fd.ratziel.module.item.api.builder.ItemMetaBuilder
import cn.fd.ratziel.module.item.api.meta.ItemDisplay
import cn.fd.ratziel.module.item.item.mapping.ItemMapping
import cn.fd.ratziel.module.item.util.asItemTagData
import cn.fd.ratziel.module.item.util.emptyTagData
import cn.fd.ratziel.module.item.util.meta.applyTo
import kotlinx.serialization.Contextual
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import net.kyori.adventure.text.Component
import org.bukkit.inventory.meta.ItemMeta
import taboolib.module.nms.ItemTag
import taboolib.module.nms.ItemTagList

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

    override fun build(meta: ItemMeta) = applyTo(meta, false)

    /**
     * 将信息写入物品标签
     */
    fun write(tag: ItemTag) {
        val display = tag.computeIfAbsent(ItemMapping.DISPLAY.get()) { emptyTagData() } as ItemTag
        display[ItemMapping.DISPLAY_NAME.get()] = name?.toJsonFormat()?.asItemTagData() ?: emptyTagData()
        display[ItemMapping.DISPLAY_LORE.get()] =
            lore?.map { it.toJsonFormat().asItemTagData() }?.let { ItemTagList(it) } ?: emptyTagData()
        display[ItemMapping.DISPLAY_LOCAL_NAME.get()] = localizedName?.toJsonFormat()?.asItemTagData() ?: emptyTagData()
    }

}
@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.module.itemengine.item.meta

import cn.fd.ratziel.common.message.buildMessage
import cn.fd.ratziel.module.itemengine.api.attribute.NBTAttribute
import cn.fd.ratziel.module.itemengine.api.builder.ItemTagBuilder
import cn.fd.ratziel.module.itemengine.api.meta.ItemDisplay
import cn.fd.ratziel.module.itemengine.mapping.ItemMapping
import cn.fd.ratziel.module.itemengine.nbt.TiNBTData
import cn.fd.ratziel.module.itemengine.util.nmsComponent
import kotlinx.serialization.Contextual
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import net.kyori.adventure.text.Component
import taboolib.module.nms.ItemTag
import taboolib.module.nms.ItemTagData
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
) : ItemDisplay, ItemTagBuilder {

    /**
     * 设置显示名称
     */
    fun setName(origin: String?) {
        name = buildMessage(origin)
    }

    /**
     * 设置描述
     */
    fun setLore(components: Iterable<String?>) {
        lore = components.map { buildMessage(it) }
    }

    /**
     * 将信息写入物品标签
     */
    override fun build(tag: ItemTag) {
        val display = tag.computeIfAbsent(ItemMapping.DISPLAY.get()) { ItemTag() } as ItemTag
        if (name != null)
            display[ItemMapping.DISPLAY_NAME.get()] = ItemTagData(nmsComponent(name)!!)
        if (lore != null)
            display[ItemMapping.DISPLAY_LORE.get()] =
                ItemTagList(lore!!.map { ItemTagData(nmsComponent(it)!!) })
        if (localizedName != null)
            display[ItemMapping.DISPLAY_LOCAL_NAME.get()] = ItemTagData(nmsComponent(localizedName)!!)
    }

    fun getNBT() = NBTAttribute(
        node = ItemMapping.DISPLAY.get(),
        value = arrayOf(
            ItemMapping.DISPLAY_NAME.get() to componentToNBT(name),
            ItemMapping.DISPLAY_LORE.get() to lore?.mapNotNull { componentToNBT(it) },
            ItemMapping.DISPLAY_LOCAL_NAME.get() to componentToNBT(name)
        )
    )

    private fun componentToNBT(component: Component?): TiNBTData? = nmsComponent(component)?.let { TiNBTData(it) }

}
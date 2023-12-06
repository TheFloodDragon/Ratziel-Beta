@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.module.itemengine.item.meta

import cn.fd.ratziel.common.message.buildMessage
import cn.fd.ratziel.module.itemengine.api.builder.ItemTagBuilder
import cn.fd.ratziel.module.itemengine.api.meta.ItemDisplay
import cn.fd.ratziel.module.itemengine.mapping.ItemMapping
import cn.fd.ratziel.module.itemengine.nbt.NBTList
import cn.fd.ratziel.module.itemengine.nbt.NBTString
import cn.fd.ratziel.module.itemengine.nbt.NBTTag
import cn.fd.ratziel.module.itemengine.util.nmsComponent
import kotlinx.serialization.Contextual
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import net.kyori.adventure.text.Component

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
    override fun build(tag: NBTTag) {
        tag.edit(ItemMapping.DISPLAY.get(), NBTTag()) { display ->
            display[ItemMapping.DISPLAY_NAME.get()] = componentToNBT(name)
            display[ItemMapping.DISPLAY_LORE.get()] = lore?.map { componentToNBT(it) }?.let { NBTList(it) }
            display[ItemMapping.DISPLAY_LOCAL_NAME.get()] = componentToNBT(localizedName)
        }
    }

    private fun componentToNBT(component: Component?): NBTString? = nmsComponent(component)?.let { NBTString(it) }

}
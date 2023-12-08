@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.module.itemengine.item.meta

import cn.fd.ratziel.common.message.buildMessage
import cn.fd.ratziel.module.itemengine.api.attribute.ItemAttribute
import cn.fd.ratziel.module.itemengine.api.builder.ItemTagBuilder
import cn.fd.ratziel.module.itemengine.api.part.meta.ItemDisplay
import cn.fd.ratziel.module.itemengine.nbt.NBTList
import cn.fd.ratziel.module.itemengine.nbt.NBTString
import cn.fd.ratziel.module.itemengine.nbt.NBTTag
import cn.fd.ratziel.module.itemengine.util.mapping.ItemMapping
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
) : ItemDisplay, ItemAttribute<VItemDisplay>(ItemMapping.DISPLAY.get()), ItemTagBuilder {

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
        tag.editShallow(ItemMapping.DISPLAY.get()) { transform(it) }
    }

    override fun transform(source: NBTTag) = source.putAll(
        ItemMapping.DISPLAY_NAME.get() to this.componentToNBT(name),
        ItemMapping.DISPLAY_LORE.get() to this.lore?.map { componentToNBT(it) }?.let { NBTList(it) },
        ItemMapping.DISPLAY_LOCAL_NAME.get() to this.componentToNBT(localizedName)
    )

    override fun detransform(input: NBTTag) {
        TODO("Not yet implemented")
    }

    private fun componentToNBT(component: Component?): NBTString? = nmsComponent(component)?.let { NBTString(it) }

}
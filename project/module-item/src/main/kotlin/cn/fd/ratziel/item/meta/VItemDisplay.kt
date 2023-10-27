@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.item.meta

import cn.fd.ratziel.adventure.buildMessageMJ
import cn.fd.ratziel.bukkit.nbt.NBTString
import cn.fd.ratziel.item.api.meta.ItemDisplay
import cn.fd.ratziel.item.api.nbt.SerializableNBT
import cn.fd.ratziel.item.nbtnode.MetaNode
import cn.fd.ratziel.item.util.nmsComponent
import kotlinx.serialization.Contextual
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import net.kyori.adventure.text.Component
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
    @JsonNames("name", "display-name", "displayname")
    override var name: @Contextual Component? = null,
    @JsonNames("loc-name", "local-name")
    override var localizedName: @Contextual Component? = null,
    @JsonNames("lores")
    override var lore: @Contextual List<Component> = emptyList(),
) : ItemDisplay,SerializableNBT{

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

    /**
     * 应用到物品标签
     */
    override fun applyTo(tag: ItemTag) {
        val display = tag.computeIfAbsent(MetaNode.DISPLAY.value) { ItemTag() } as ItemTag
        nmsComponent(name)?.let { display[MetaNode.NAME.value] = NBTString(it) }
        nmsComponent(localizedName)?.let { display[MetaNode.LOCAL_NAME.value] = NBTString(it) }
        display[MetaNode.LORE.value] =
            lore.mapNotNull { c -> nmsComponent(c)?.let { NBTString(it) } }.toCollection(ItemTagList())
    }

}
@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.item.meta

import cn.fd.ratziel.adventure.buildMessageMJ
import cn.fd.ratziel.item.api.ItemDisplay
import cn.fd.ratziel.item.api.nbt.ItemTagAdder
import cn.fd.ratziel.item.api.nbt.ItemTagTranslator
import cn.fd.ratziel.item.util.nmsComponent
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
    @JsonNames("name", "display-name", "displayname")
    override var name: @Contextual Component? = null,
    @JsonNames("loc-name", "local-name") // TODO NBT转化
    override var localizedName: @Contextual Component? = null,
    @JsonNames("lores")
    override var lore: @Contextual List<Component> = emptyList(),
) : ItemDisplay, ItemTagAdder, ItemTagTranslator {

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
     * {display:{#toItemTag()}}
     */
    override fun applyTo(source: ItemTag) {
        source["display"] = toItemTag()
    }

    /**
     * 转化为物品标签
     * {#applyName,#applyLore}
     */
    override fun toItemTag() = ItemTag().apply {
        applyName(this)
        applyLore(this)
    }

    /**
     * 应用显示名称
     * {Name:""}
     */
    fun applyName(display: ItemTag) {
        nmsComponent(name)?.let { display["Name"] = ItemTagData(it) }
    }

    /**
     * 应用显示描述
     * {Lore:[""]}
     */
    fun applyLore(display: ItemTag) {
        display["Lore"] = lore.mapNotNull { c -> nmsComponent(c)?.let { ItemTagData(it) } }.toCollection(ItemTagList())
    }

}
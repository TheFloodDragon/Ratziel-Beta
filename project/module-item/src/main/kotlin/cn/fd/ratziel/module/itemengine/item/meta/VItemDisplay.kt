@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.module.itemengine.item.meta

import cn.fd.ratziel.common.message.buildMessage
import cn.fd.ratziel.module.itemengine.api.attribute.ItemAttribute
import cn.fd.ratziel.module.itemengine.api.attribute.NBTTransformer
import cn.fd.ratziel.module.itemengine.api.part.meta.ItemDisplay
import cn.fd.ratziel.module.item.reflex.ItemMapping
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
    override var lore: @Contextual List<Component>? = null,
) : ItemDisplay, ItemAttribute<VItemDisplay> {

    /**
     * 设置显示名称
     */
    fun setName(origin: String?) {
        name = buildMessage(origin)
    }

    /**
     * 设置描述
     */
    fun setLore(components: Iterable<String?>?) {
        lore = components?.map { buildMessage(it) }
    }

    /**
     * 设置本地化名称
     */
    fun setLocalizedName(origin: String?) {
        localizedName = buildMessage(origin)
    }

    override val transformer get() = Companion

    companion object : NBTTransformer<VItemDisplay> {

        override fun transform(target: VItemDisplay, source: NBTTag): NBTTag = target.run {
            source.putAll(
                ItemMapping.DISPLAY_NAME.get() to componentToNBT(name),
                ItemMapping.DISPLAY_LORE.get() to lore?.map { componentToNBT(it) }?.let { NBTList(it) },
                ItemMapping.DISPLAY_LOCAL_NAME.get() to componentToNBT(localizedName)
            )
        }

        override fun detransform(target: VItemDisplay, from: NBTTag) = target.run {
            setName((from[ItemMapping.DISPLAY_NAME.get()] as? NBTString)?.content)
            setLore((from[ItemMapping.DISPLAY_LORE.get()] as? NBTList)?.content?.mapNotNull { (it as? NBTString)?.content })
            setLocalizedName((from[ItemMapping.DISPLAY_LOCAL_NAME.get()] as? NBTString)?.content)
        }

        private fun componentToNBT(component: Component?): NBTString? =
            nmsComponent(component)?.let { NBTString(it) }

    }

    override val node get() = ItemMapping.DISPLAY.get()

}
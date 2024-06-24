@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.module.item.impl.component

import cn.fd.ratziel.common.message.Message
import cn.fd.ratziel.common.message.MessageComponent
import cn.fd.ratziel.core.serialization.EnhancedList
import cn.fd.ratziel.core.util.putNonNull
import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.ItemTransformer
import cn.fd.ratziel.module.item.impl.OccupyNode
import cn.fd.ratziel.module.item.api.part.ItemDisplay
import cn.fd.ratziel.module.item.impl.TheItemData
import cn.fd.ratziel.module.item.nbt.NBTList
import cn.fd.ratziel.module.item.nbt.NBTString
import cn.fd.ratziel.module.item.nbt.addAll
import cn.fd.ratziel.module.item.nms.ItemSheet
import cn.fd.ratziel.module.item.util.castThen
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import net.kyori.adventure.text.Component
import taboolib.module.nms.MinecraftVersion

/**
 * VItemDisplay
 *
 * @author TheFloodDragon
 * @since 2024/3/16 11:27
 */
@Serializable
data class VItemDisplay(
    @JsonNames("name", "display-name", "displayName")
    override var name: MessageComponent? = null,
    @JsonNames("loc-name", "locName", "local-name", "localName")
    override var localizedName: MessageComponent? = null,
    @JsonNames("lores")
    override var lore: EnhancedList<MessageComponent>? = null,
) : ItemDisplay {

    override fun setName(name: String) {
        this.name = Message.buildMessage(name)
    }

    override fun setLore(lore: Iterable<String>) {
        this.lore = lore.map { Message.buildMessage(it) }
    }

    override fun setLocalizedName(localizedName: String) {
        this.localizedName = Message.buildMessage(localizedName)
    }

    override fun getNode() = node

    override fun transform(source: ItemData) {
        source.tag.addAll(
            ItemSheet.DISPLAY_NAME to componentToData(this.name),
            ItemSheet.DISPLAY_LORE to this.lore?.mapNotNull { componentToData(it) }?.let { NBTList(it) },
            ItemSheet.DISPLAY_LOCAL_NAME to componentToData(this.localizedName)
        )
    }

    override fun detransform(target: ItemData) {
        // Universal
        target.tag[ItemSheet.DISPLAY_NAME].castThen<NBTString> {
            this.setName(it.content)
        }
        target.tag[ItemSheet.DISPLAY_LORE].castThen<NBTList> {
            this.setLore(it.content.mapNotNull { line -> (line as? NBTString)?.content })
        }
        target.tag[ItemSheet.DISPLAY_LOCAL_NAME].castThen<NBTString> {
            this.setLocalizedName(it.content)
        }
    }

    companion object : ItemTransformer<ItemDisplay> {

        override val node =
            if (MinecraftVersion.majorLegacy >= 12005) OccupyNode.APEX_NODE
            else OccupyNode(ItemSheet.DISPLAY, OccupyNode.APEX_NODE)

        internal fun componentToData(component: Component?): NBTString? = component?.let { NBTString(transformComponent(it)) }

        /**
         * Type:
         *   1.13+ > Json Format
         *   1.13- > Original Format (§)
         */
        fun transformComponent(component: Component): String =
            if (MinecraftVersion.isLower(MinecraftVersion.V1_13)) {
                Message.wrapper.legacyBuilder.serialize(component)
            } else Message.transformToJson(component)


        override fun transform(component: ItemDisplay) = TheItemData().apply {
            tag.putNonNull(ItemSheet.DISPLAY_NAME, componentToData(component.name))
            tag.putNonNull(ItemSheet.DISPLAY_LORE, component.lore?.mapNotNull { componentToData(it) }?.let { NBTList(it) })
            tag.putNonNull(ItemSheet.DISPLAY_LOCAL_NAME, componentToData(component.localizedName))
        }

        override fun detransform(data: ItemData): ItemDisplay = VItemDisplay().apply {
            data.castThen<NBTString>(ItemSheet.DISPLAY_NAME) { this.setName(it.content) }
            data.castThen<NBTList>(ItemSheet.DISPLAY_LORE) {
                this.setLore(it.content.mapNotNull { line -> (line as? NBTString)?.content })
            }
            data.castThen<NBTString>(ItemSheet.DISPLAY_LOCAL_NAME) { this.setLocalizedName(it.content) }
        }

    }

}
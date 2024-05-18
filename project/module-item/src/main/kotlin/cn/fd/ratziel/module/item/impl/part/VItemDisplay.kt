@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.module.item.impl.part

import cn.fd.ratziel.common.message.Message
import cn.fd.ratziel.common.message.MessageComponent
import cn.fd.ratziel.core.serialization.EnhancedList
import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.common.OccupyNode
import cn.fd.ratziel.module.item.api.part.ItemDisplay
import cn.fd.ratziel.module.item.nbt.NBTList
import cn.fd.ratziel.module.item.nbt.NBTString
import cn.fd.ratziel.module.item.nbt.addAll
import cn.fd.ratziel.module.item.reflex.ItemSheet
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
            itemNameNode to componentToData(this.localizedName)
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
        target.tag[itemNameNode].castThen<NBTString> {
            this.setLocalizedName(it.content)
        }
    }

    companion object {

        internal val node by lazy {
            if (MinecraftVersion.majorLegacy >= 12005) OccupyNode.APEX_NODE else OccupyNode(ItemSheet.DISPLAY, OccupyNode.APEX_NODE)
        }

        internal val itemNameNode by lazy {
            if (MinecraftVersion.majorLegacy >= 12005) ItemSheet.ITEM_NAME else ItemSheet.DISPLAY_LOCAL_NAME
        }

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

    }

}
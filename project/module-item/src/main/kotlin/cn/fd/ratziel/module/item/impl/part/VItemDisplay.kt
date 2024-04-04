@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.module.item.impl.part

import cn.fd.ratziel.common.message.Message
import cn.fd.ratziel.common.message.MessageComponent
import cn.fd.ratziel.module.item.api.common.OccupyNode
import cn.fd.ratziel.module.item.api.common.SimpleDataTransformer
import cn.fd.ratziel.module.item.api.part.ItemDisplay
import cn.fd.ratziel.module.item.nbt.NBTCompound
import cn.fd.ratziel.module.item.nbt.NBTList
import cn.fd.ratziel.module.item.nbt.NBTString
import cn.fd.ratziel.module.item.reflex.ItemMapping
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
    override var lore: List<MessageComponent>? = null,
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

    override fun transformer() = if (MinecraftVersion.isLower(12005)) TransformerLow else TransformerHigh

    object TransformerLow : SimpleDataTransformer<ItemDisplay> {

        override val node = OccupyNode(ItemMapping.DISPLAY.mapping, OccupyNode.APEX_NODE)

        override fun transform(target: ItemDisplay, source: NBTCompound) = source.putAll(
            ItemMapping.DISPLAY_NAME.mapping to componentToData(target.name),
            ItemMapping.DISPLAY_LORE.mapping to target.lore?.map { componentToData(it)!!.getData() }?.let { NBTList(NBTList.new(ArrayList(it))) },
            ItemMapping.DISPLAY_LOCAL_NAME.mapping to componentToData(target.localizedName)
        )

        override fun detransform(target: ItemDisplay, from: NBTCompound): Unit = target.run {
            (from[ItemMapping.DISPLAY_NAME.mapping] as? NBTString)?.let { setName(it.content) }
            (from[ItemMapping.DISPLAY_LORE.mapping] as? NBTList)?.let { setLore(it.content.mapNotNull { line -> (line as? NBTString)?.content }) }
            (from[ItemMapping.DISPLAY_LOCAL_NAME.mapping] as? NBTString)?.let { setLocalizedName(it.content) }
        }

        internal fun componentToData(component: Component?): NBTString? = component?.let { NBTString(NBTString.new(transformComponent(it))) }

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

    object TransformerHigh : SimpleDataTransformer<ItemDisplay> {

        override val node = OccupyNode.APEX_NODE

        override fun transform(target: ItemDisplay, source: NBTCompound) = source.apply {
            TODO("Not yet implemented")
        }

        override fun detransform(target: ItemDisplay, from: NBTCompound) = target.run {
            TODO("Not yet implemented")
        }

    }

}
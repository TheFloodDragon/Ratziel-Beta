package cn.fd.ratziel.module.item.impl.part

import cn.fd.ratziel.common.message.MessageComponent
import cn.fd.ratziel.common.message.buildMessage
import cn.fd.ratziel.module.item.api.common.DataSimpleTransformer
import cn.fd.ratziel.module.item.api.common.OccupyNode
import cn.fd.ratziel.module.item.api.part.ItemDisplay
import cn.fd.ratziel.module.item.nbt.NBTCompound
import cn.fd.ratziel.module.item.reflex.ItemMapping
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
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
        this.name = buildMessage(name)
    }

    override fun setLore(lore: Iterable<String>) {
        this.lore = lore.map { buildMessage(it) }
    }

    override fun setLocalizedName(localizedName: String) {
        this.localizedName = buildMessage(localizedName)
    }

    override fun transformer() = if (MinecraftVersion.isLower(12005)) TransformerLow else TransformerHigh

    object TransformerLow : DataSimpleTransformer<ItemDisplay> {

        override val node = OccupyNode(ItemMapping.DISPLAY.mapping, OccupyNode.APEX_NODE)

        override fun transform(target: ItemDisplay, source: NBTCompound) = source.apply {
            TODO("Not yet implemented")
        }

        override fun detransform(input: ItemDisplay, from: NBTCompound) = input.run {
            TODO("Not yet implemented")
        }

    }

    object TransformerHigh : DataSimpleTransformer<ItemDisplay> {

        override val node = OccupyNode.APEX_NODE

        override fun transform(target: ItemDisplay, source: NBTCompound) = source.apply {
            TODO("Not yet implemented")
        }

        override fun detransform(input: ItemDisplay, from: NBTCompound) = input.run {
            TODO("Not yet implemented")
        }

    }

}
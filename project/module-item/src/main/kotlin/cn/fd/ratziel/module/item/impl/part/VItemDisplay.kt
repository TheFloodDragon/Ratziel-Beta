package cn.fd.ratziel.module.item.impl.part

import cn.fd.ratziel.common.message.MessageComponent
import cn.fd.ratziel.common.message.buildMessage
import cn.fd.ratziel.module.item.api.NodeDistributor
import cn.fd.ratziel.module.item.api.common.DataCTransformer
import cn.fd.ratziel.module.item.api.part.ItemDisplay
import cn.fd.ratziel.module.item.nbt.NBTCompound
import kotlinx.serialization.Serializable

/**
 * VItemDisplay
 *
 * @author TheFloodDragon
 * @since 2024/3/16 11:27
 */
@Serializable
data class VItemDisplay(
    override var name: MessageComponent? = null,
    override var localizedName: MessageComponent? = null,
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

    override fun getNodeDistributor(): NodeDistributor {
        TODO("Not yet implemented")
    }

    override fun getTransformer() = DisplayTransformer

}

object DisplayTransformer : DataCTransformer<ItemDisplay> {

    override fun transform(target: ItemDisplay, source: NBTCompound) = source.apply {
        TODO("Not yet implemented")
    }

    override fun detransform(input: ItemDisplay, from: NBTCompound) = input.run {
        TODO("Not yet implemented")
    }

}
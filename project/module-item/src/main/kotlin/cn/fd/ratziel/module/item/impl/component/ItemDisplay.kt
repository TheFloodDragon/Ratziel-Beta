@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.module.item.impl.component

import cn.fd.ratziel.common.message.Message
import cn.fd.ratziel.common.message.MessageComponent
import cn.fd.ratziel.core.serialization.EnhancedList
import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.builder.ItemTransformer
import cn.fd.ratziel.module.item.nbt.NBTList
import cn.fd.ratziel.module.item.nbt.NBTString
import cn.fd.ratziel.module.item.nms.ItemSheet
import cn.fd.ratziel.module.item.util.read
import cn.fd.ratziel.module.item.util.write
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import net.kyori.adventure.text.Component
import taboolib.module.nms.MinecraftVersion

/**
 * ItemDisplay - 物品显示
 *
 * @author TheFloodDragon
 * @since 2024/3/16 11:27
 */
@Serializable
data class ItemDisplay(
    /**
     * 物品名称
     */
    @JsonNames("name", "display-name", "displayName")
    var name: MessageComponent? = null,
    /**
     * 物品本地化名称
     */
    @JsonNames("loc-name", "locName", "local-name", "localName")
    var localizedName: MessageComponent? = null,
    /**
     * 物品描述
     */
    @JsonNames("lores")
    var lore: EnhancedList<MessageComponent>? = null,
) {

    /**
     * 设置名称
     */
    fun setName(name: String) {
        this.name = Message.buildMessage(name)
    }

    /**
     * 设置描述
     */
    fun setLore(lore: Iterable<String>) {
        this.lore = lore.map { Message.buildMessage(it) }
    }

    /**
     * 设置物品本地化名称
     */
    fun setLocalizedName(localizedName: String) {
        this.localizedName = Message.buildMessage(localizedName)
    }

    companion object : ItemTransformer<ItemDisplay> {

        override fun transform(data: ItemData.Mutable, component: ItemDisplay) {
            data.write(ItemSheet.DISPLAY_NAME, componentToData(component.name))
            data.write(ItemSheet.DISPLAY_LORE, component.lore?.mapNotNull { componentToData(it) }?.let { NBTList(it) })
            data.write(ItemSheet.DISPLAY_LOCAL_NAME, componentToData(component.localizedName))
        }

        override fun detransform(data: ItemData): ItemDisplay = ItemDisplay().apply {
            data.read<NBTString>(ItemSheet.DISPLAY_NAME) { this.setName(it.content) }
            data.read<NBTList>(ItemSheet.DISPLAY_LORE) {
                this.setLore(it.content.mapNotNull { line -> (line as? NBTString)?.content })
            }
            data.read<NBTString>(ItemSheet.DISPLAY_LOCAL_NAME) { this.setLocalizedName(it.content) }
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
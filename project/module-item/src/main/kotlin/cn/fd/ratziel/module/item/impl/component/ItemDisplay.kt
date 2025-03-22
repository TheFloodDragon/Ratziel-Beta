@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.module.item.impl.component

import cn.altawk.nbt.tag.NbtCompound
import cn.fd.ratziel.common.message.Message
import cn.fd.ratziel.common.message.MessageComponent
import cn.fd.ratziel.module.item.api.event.ItemGenerateEvent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import taboolib.common.platform.function.registerBukkitListener
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
    var lore: List<MessageComponent>? = null,
) {

    /**
     * 设置名称
     */
    fun setName(name: String) {
        this.name = Message.buildMessage(name)
    }

    /**
     * 设置物品本地化名称
     */
    fun setLocalizedName(localizedName: String) {
        this.localizedName = Message.buildMessage(localizedName)
    }

    /**
     * 设置描述
     */
    fun setLore(lore: Iterable<String>) {
        this.lore = lore.map { Message.buildMessage(it) }
    }

    companion object {

        init {
            // 低版本支持 (1.20.5-)
            if (MinecraftVersion.isLower(12005)) {
                registerBukkitListener(ItemGenerateEvent.DataGenerate::class.java) { event ->
                    if (event.componentType != ItemDisplay::class.java) return@registerBukkitListener
                    val originTag = event.generatedTag ?: return@registerBukkitListener
                    event.generatedTag = NbtCompound { put("display", originTag) }
                }
            }
        }

    }

}
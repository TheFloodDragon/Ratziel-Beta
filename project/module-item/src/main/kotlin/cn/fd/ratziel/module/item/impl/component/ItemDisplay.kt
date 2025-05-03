@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.module.item.impl.component

import cn.fd.ratziel.common.message.Message
import cn.fd.ratziel.common.message.MessageComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

/**
 * ItemDisplay - 物品显示
 *
 * @author TheFloodDragon
 * @since 2024/3/16 11:27
 */
@Serializable
class ItemDisplay(
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

}
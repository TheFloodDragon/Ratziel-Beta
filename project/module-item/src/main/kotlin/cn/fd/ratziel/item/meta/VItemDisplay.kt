@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.item.meta

import cn.fd.ratziel.item.api.ItemDisplay
import cn.fd.ratziel.item.meta.util.nmsComponent
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
    @JsonNames("name", "display-name", "displayname")
    override var name: String? = null,
    @JsonNames("lores")
    override var lore: List<String> = emptyList(),
) : ItemDisplay {

    /**
     * 设置显示名称
     */
    fun setName(component: Component) {
        name = nmsComponent(component)
    }

    /**
     * 设置描述
     */
    fun setLore(components: Iterable<Component>) {
        lore = components.map { nmsComponent(it) }
    }

}
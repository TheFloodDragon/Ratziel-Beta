package cn.fd.ratziel.item.meta

import cn.fd.ratziel.item.api.ItemDisplay
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.kyori.adventure.text.Component

/**
 * VItemDisplay
 *
 * @author TheFloodDragon
 * @since 2023/10/14 16:15
 */
@Serializable
data class VItemDisplay(
    private val nameC: ComponentSerializable,
    private val loreC: Iterable<ComponentSerializable>?,
) : ItemDisplay {

    @Transient
    override var name: String? = nmsComponent(nameC)

    @Transient
    override var lore: List<String> = loreC?.map { nmsComponent(it) } ?: emptyList()

    /**
     * 设置显示名称
     */
    fun setDisplayName(component: Component) {
        name = nmsComponent(component)
    }

    /**
     * 设置描述
     */
    fun setLore(components: Iterable<Component>) {
        lore = components.map { nmsComponent(it) }
    }


}
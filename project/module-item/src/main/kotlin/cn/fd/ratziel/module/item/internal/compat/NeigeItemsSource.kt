package cn.fd.ratziel.module.item.internal.compat

import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.functional.ArgumentContext
import cn.fd.ratziel.core.serialization.json.getBy
import cn.fd.ratziel.module.item.api.NeoItem
import cn.fd.ratziel.module.item.api.builder.ItemSource
import cn.fd.ratziel.module.item.internal.nms.RefItemStack
import cn.fd.ratziel.platform.bukkit.util.player
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull

/**
 * NeigeItemsSource
 *
 * @author TheFloodDragon
 * @since 2025/5/25 11:23
 */
object NeigeItemsSource : ItemSource {

    private val alias = listOf("ni", "NeigeItems")

    override fun generateItem(element: Element, context: ArgumentContext): NeoItem? {
        // 获取名称
        val property = (element.property as? JsonObject) ?: return null
        val name = (property.getBy(alias) as? JsonPrimitive)?.contentOrNull ?: return null
        // 生成物品
        val generator = pers.neige.neigeitems.manager.ItemManager.getItem(name) ?: return null
        val itemStack = generator.getItemStack(context.player(), mutableMapOf()) ?: return null
        // 提取数据
        val data = RefItemStack.of(itemStack).extractData()
        return CompatItem(NeigeItemsHook.pluginName, data)
    }

}
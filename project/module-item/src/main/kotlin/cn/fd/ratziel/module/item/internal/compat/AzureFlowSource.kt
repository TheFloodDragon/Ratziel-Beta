package cn.fd.ratziel.module.item.internal.compat

import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.functional.ArgumentContext
import cn.fd.ratziel.core.serialization.json.getBy
import cn.fd.ratziel.module.item.api.NeoItem
import cn.fd.ratziel.module.item.api.builder.ItemSource
import cn.fd.ratziel.module.item.internal.nms.RefItemStack
import cn.fd.ratziel.platform.bukkit.util.player
import io.rokuko.azureflow.api.AzureFlowAPI
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import org.bukkit.entity.Player

/**
 * AzureFlowSource
 *
 * @author TheFloodDragon
 * @since 2025/4/4 15:26
 */
object AzureFlowSource : ItemSource {

    private val alias = listOf("af", "AzureFlow")

    override fun generateItem(element: Element, context: ArgumentContext): NeoItem? {
        // 获取名称
        val property = (element.property as? JsonObject) ?: return null
        val name = (property.getBy(alias) as? JsonPrimitive)?.contentOrNull ?: return null
        // 生成物品
        val factory = AzureFlowAPI.getFactory(name) ?: return null
        val itemStack = factory.build().itemStack(context.player() as? Player)
        // 提取数据
        val data = RefItemStack.of(itemStack).extractData()
        return CompatItem(AzureFlowHook.pluginName, data)
    }

}
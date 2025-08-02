package cn.fd.ratziel.module.compat.impl

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
import net.momirealms.craftengine.bukkit.api.CraftEngineItems
import net.momirealms.craftengine.bukkit.plugin.BukkitCraftEngine
import net.momirealms.craftengine.core.util.Key
import org.bukkit.entity.Player

/**
 * CraftEngineSource
 *
 * @author TheFloodDragon
 * @since 2025/8/2 20:18
 */
object CraftEngineSource : ItemSource {

    private val alias = listOf("ce", "CraftEngine")

    override fun generateItem(element: Element, context: ArgumentContext): NeoItem? {
        // 获取名称
        val property = (element.property as? JsonObject) ?: return null
        val name = (property.getBy(alias) as? JsonPrimitive)?.contentOrNull ?: return null
        // 生成物品
        val player = (context.player() as? Player)?.let { BukkitCraftEngine.instance().adapt(it) }
        val itemStack = CraftEngineItems.byId(Key.of(name))?.buildItemStack(player) ?: return null
        // 提取数据
        val data = RefItemStack.of(itemStack).extractData()
        return CompatItem(CraftEngineHook.pluginName, data)
    }

}
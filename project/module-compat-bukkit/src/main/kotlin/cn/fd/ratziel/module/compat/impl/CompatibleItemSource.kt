package cn.fd.ratziel.module.compat.impl

import cn.fd.ratziel.core.util.getBy
import cn.fd.ratziel.module.item.api.builder.ItemSource
import cn.fd.ratziel.module.item.internal.nms.RefItemStack
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.warning

/**
 * CompatibleItemSource
 *
 * @author TheFloodDragon
 * @since 2025/9/6 20:05
 */
abstract class CompatibleItemSource(
    val pluginName: String,
    vararg val alias: String,
) : ItemSource {

    /**
     * 判断一个物品是不是该插件的
     */
    abstract fun isMine(item: ItemStack): Boolean

    fun readName(element: JsonElement): String? {
        val property = (element as? JsonObject) ?: return null
        return (property.getBy(*alias) as? JsonPrimitive)?.contentOrNull
            ?: (property[pluginName] as? JsonPrimitive)?.contentOrNull
    }

    fun <T> T.warnOnNull(name: String): T? {
        if (this == null) {
            warning("Item named '$name' with source $pluginName not found.")
            return null
        } else return this
    }

    fun ItemStack.asCompatible(): CompatibleItem {
        val data = RefItemStack.of(this).extractData()
        return CompatibleItem(pluginName, data)
    }

}
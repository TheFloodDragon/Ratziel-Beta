package cn.fd.ratziel.module.item.impl.builder.provided

import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.functional.ArgumentContext
import cn.fd.ratziel.core.serialization.json.getBy
import cn.fd.ratziel.module.item.api.NeoItem
import cn.fd.ratziel.module.item.api.builder.ItemSource
import cn.fd.ratziel.module.item.impl.SimpleItem
import cn.fd.ratziel.module.item.internal.nms.RefItemStack
import cn.fd.ratziel.module.item.util.SkullUtil
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull

/**
 * SkullSource
 *
 * @author TheFloodDragon
 * @since 2025/5/25 08:58
 */
object SkullSource : ItemSource.Named {

    override val names = arrayOf("skull", "head")

    override fun generateItem(element: Element, context: ArgumentContext): NeoItem? {
        val property = (element.property as? JsonObject) ?: return null
        // 头颅数据值
        val value = (property.getBy(*names) as? JsonPrimitive)?.contentOrNull ?: return null

        // 创建物品
        val itemStack = SkullUtil.fetchSkull(value)
        val data = RefItemStack.of(itemStack).extractData()

        // 创建仅使用数据功能的物品
        return SimpleItem(data)
    }

}
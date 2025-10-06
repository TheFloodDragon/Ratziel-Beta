package cn.fd.ratziel.module.item.impl.builder.provided

import cn.altawk.nbt.tag.NbtCompound
import cn.fd.ratziel.common.element.registry.AutoRegister
import cn.fd.ratziel.core.contextual.ArgumentContext
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.module.item.ItemElement
import cn.fd.ratziel.module.item.api.NeoItem
import cn.fd.ratziel.module.item.api.builder.ItemSource
import cn.fd.ratziel.module.item.impl.SimpleData
import cn.fd.ratziel.module.item.impl.SimpleItem
import cn.fd.ratziel.module.nbt.NbtSerializer
import kotlinx.serialization.json.JsonObject

/**
 * NbtTagSource
 *
 * @author TheFloodDragon
 * @since 2025/6/6 22:35
 */
@AutoRegister
object NbtTagSource : ItemSource {

    override fun generateItem(element: Element, context: ArgumentContext): NeoItem? {
        val property = (element.property as? JsonObject) ?: return null
        val json = (property["nbt"] ?: property["components"]) as? JsonObject ?: return null
        // 转化成 Nbt 标签
        val tag = ItemElement.json.decodeFromJsonElement(NbtSerializer, json)
        val data = SimpleData(tag = tag as? NbtCompound ?: return null)
        // 创建仅使用数据功能的物品
        return SimpleItem(data)
    }

}
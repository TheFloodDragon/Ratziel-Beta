package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.core.SimpleIdentifier
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.core.serialization.getBy
import cn.fd.ratziel.core.util.digest
import cn.fd.ratziel.module.item.RatzielItem
import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.builder.ItemSource
import cn.fd.ratziel.module.item.util.MetaMatcher
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull

/**
 * NativeSource - Ratziel 原生物品源
 *
 * @author TheFloodDragon
 * @since 2025/4/4 14:24
 */
object NativeSource : ItemSource {

    val materialNames = listOf("material", "mat", "materials", "mats")

    override fun generateItem(element: Element, sourceData: ItemData, context: ArgumentContext): RatzielItem? {
        // 生成物品唯一标识符
        val identifier = SimpleIdentifier(element.name)
        // 确定版本
        val property = (element.property as? JsonObject) ?: return null
        val version = property.toString().digest()
        // 生成物品信息
        val info = RatzielItem.Info(identifier, version)

        // 确定第一物品材料类型
        val name = (property.getBy(materialNames) as? JsonPrimitive)?.contentOrNull ?: return null
        sourceData.material = MetaMatcher.matchMaterial(name)

        // 创建物品
        return RatzielItem.of(info, sourceData)
    }

}
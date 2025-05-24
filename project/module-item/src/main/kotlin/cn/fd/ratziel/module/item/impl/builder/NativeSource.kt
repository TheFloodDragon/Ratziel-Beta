package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.core.SimpleIdentifier
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.core.serialization.json.getBy
import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.NeoItem
import cn.fd.ratziel.module.item.api.builder.ItemSource
import cn.fd.ratziel.module.item.api.service.ItemService
import cn.fd.ratziel.module.item.impl.RatzielItem
import cn.fd.ratziel.module.item.impl.SimpleData
import cn.fd.ratziel.module.item.util.MetaMatcher
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import taboolib.common.io.digest

/**
 * NativeSource - Ratziel 原生物品源 (不经过注册机制)
 *
 * @author TheFloodDragon
 * @since 2025/4/4 14:24
 */
object NativeSource {

    fun generateItem(element: Element, sourceData: ItemData): RatzielItem? {
        // 生成物品唯一标识符
        val identifier = SimpleIdentifier(element.name)
        // 确定版本
        val property = (element.property as? JsonObject) ?: return null
        val version = property.toString().digest("SHA-256")
        // 生成物品信息
        val info = RatzielItem.Info(identifier, version)

        // 创建物品
        return RatzielItem.of(info, sourceData)
    }

    object MaterialSource : ItemSource {

        val materialNames = listOf("material", "mat", "materials", "mats")

        override fun generateItem(element: Element, context: ArgumentContext): NeoItem? {
            val data = SimpleData()

            // 确定第一物品材料类型
            val property = (element.property as? JsonObject) ?: return null
            val name = (property.getBy(materialNames) as? JsonPrimitive)?.contentOrNull
            if (name != null) data.material = MetaMatcher.matchMaterial(name)

            // 创建仅使用数据功能的物品
            return object : NeoItem {
                override val data: ItemData = data
                override val service: ItemService
                    get() = throw UnsupportedOperationException("Service is not supported for MaterialSource's item!")
            }
        }

    }

}
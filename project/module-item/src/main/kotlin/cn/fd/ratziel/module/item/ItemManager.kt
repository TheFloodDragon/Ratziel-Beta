package cn.fd.ratziel.module.item

import cn.fd.ratziel.core.element.ElementIdentifier
import cn.fd.ratziel.module.item.api.builder.ItemGenerator
import java.util.concurrent.ConcurrentHashMap

/**
 * ItemManager
 *
 * @author TheFloodDragon
 * @since 2024/2/1 10:41
 */
object ItemManager {

    /**
     * 物品注册表
     */
    val registry: MutableMap<ElementIdentifier, ItemGenerator> = ConcurrentHashMap()

    /**
     * 通过物品标识符名称获取物品生成器
     */
    fun getByName(name: String): ItemGenerator? {
        for (entry in registry) {
            if (entry.key.name == name) return entry.value
        }
        return null
    }

}
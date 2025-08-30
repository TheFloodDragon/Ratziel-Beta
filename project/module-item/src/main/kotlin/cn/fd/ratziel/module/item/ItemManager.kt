package cn.fd.ratziel.module.item

import cn.fd.ratziel.core.Identifier
import cn.fd.ratziel.core.contextual.ArgumentContext
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
    val registry: MutableMap<String, ItemGenerator> = ConcurrentHashMap()

    /**
     * 获取生成器的上下文
     */
    fun generatorContext(identifier: Identifier): ArgumentContext? {
        return registry[identifier.content]?.contextProvider?.newContext()
    }

}
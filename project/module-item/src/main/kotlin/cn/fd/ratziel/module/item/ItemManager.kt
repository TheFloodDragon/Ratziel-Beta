package cn.fd.ratziel.module.item

import cn.fd.ratziel.core.Identifier
import cn.fd.ratziel.core.contextual.ArgumentContext
import cn.fd.ratziel.core.contextual.SimpleContext
import cn.fd.ratziel.module.item.api.builder.ItemGenerator
import cn.fd.ratziel.module.item.api.builder.ItemInterpreter
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
     * 获取物品生成器
     */
    fun generator(identifier: Identifier): ItemGenerator {
        return this.registry[identifier.content] ?: throw NoSuchElementException("ItemGenerator for ${identifier.content} not found.")
    }

    /**
     * 获取物品生成器的上下文
     */
    fun generatorContext(identifier: Identifier): ArgumentContext {
        return registry[identifier.content]?.contextProvider?.get() ?: SimpleContext()
    }

    /**
     * 获取物品生成器的指定类型的解释器
     */
    inline fun <reified T : ItemInterpreter> generatorInterpreter(identifier: Identifier) = generatorInterpreter(T::class.java, identifier)

    /**
     * 获取物品生成器的指定类型的解释器
     */
    fun <T : ItemInterpreter> generatorInterpreter(type: Class<T>, identifier: Identifier): T {
        return this.generator(identifier).compositor.getInterpreter(type)
    }

}
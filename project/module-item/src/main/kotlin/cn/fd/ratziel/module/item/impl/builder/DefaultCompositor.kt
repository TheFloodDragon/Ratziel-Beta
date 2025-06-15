package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.module.item.ItemRegistry
import cn.fd.ratziel.module.item.api.builder.InterpreterCompositor
import cn.fd.ratziel.module.item.api.builder.ItemInterpreter

/**
 * DefaultCompositor
 *
 * @author TheFloodDragon
 * @since 2025/6/15 09:37
 */
class DefaultCompositor : InterpreterCompositor {

    val interpretersMap: Map<Class<*>, ItemInterpreter> by lazy {
        ItemRegistry.interpreters.map { it.get() } // 创建每一个单例
            .associateBy { it::class.java }
    }

    override val interpreters: Iterable<ItemInterpreter>
        get() = interpretersMap.values

    override fun <T : ItemInterpreter> get(type: Class<T>): T {
        val find = interpretersMap[type]
            ?: throw NoSuchElementException("Cannot find interpreter named '${type.simpleName}'!")
        @Suppress("UNCHECKED_CAST")
        return find as T
    }

}
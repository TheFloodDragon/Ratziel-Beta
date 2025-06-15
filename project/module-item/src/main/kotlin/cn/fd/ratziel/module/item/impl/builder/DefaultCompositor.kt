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

     override val interpreters = ItemRegistry.interpreters.map { it.get() } // 创建每一个单例

    override fun <T : ItemInterpreter> get(type: Class<T>): T {
        val find = interpreters.find { type.isAssignableFrom(it::class.java) }
            ?: throw NoSuchElementException("Cannot find interpreter named '${type.simpleName}'!")
        @Suppress("UNCHECKED_CAST")
        return find as T
    }

}
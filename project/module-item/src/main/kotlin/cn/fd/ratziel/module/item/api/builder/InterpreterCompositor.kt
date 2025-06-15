package cn.fd.ratziel.module.item.api.builder

/**
 * InterpreterCompositor
 *
 * @author TheFloodDragon
 * @since 2025/6/15 09:33
 */
interface InterpreterCompositor {

    /**
     * 所有的解释器列表
     */
    val interpreters: Iterable<ItemInterpreter>

    /**
     * 获取解释器
     */
    fun <T : ItemInterpreter> get(type: Class<T>): T

}
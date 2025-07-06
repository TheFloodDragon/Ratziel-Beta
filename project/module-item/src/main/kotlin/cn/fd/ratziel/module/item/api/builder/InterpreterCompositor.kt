package cn.fd.ratziel.module.item.api.builder

/**
 * InterpreterCompositor
 *
 * @author TheFloodDragon
 * @since 2025/6/15 09:33
 */
interface InterpreterCompositor {

    suspend fun runTask(stream: ItemStream)

}
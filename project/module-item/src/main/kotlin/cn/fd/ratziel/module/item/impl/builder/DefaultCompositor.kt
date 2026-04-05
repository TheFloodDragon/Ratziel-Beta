package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.module.item.ItemRegistry
import cn.fd.ratziel.module.item.api.builder.ItemCompositor
import cn.fd.ratziel.module.item.api.builder.ItemInterpreter
import cn.fd.ratziel.module.item.api.builder.ItemSource
import cn.fd.ratziel.module.item.api.builder.ItemStream
import cn.fd.ratziel.module.item.impl.builder.provided.SourceInterpreter
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import taboolib.common.platform.function.debug
import kotlin.system.measureTimeMillis

/**
 * DefaultCompositor
 *
 * @author TheFloodDragon
 * @since 2025/6/15 09:37
 */
class DefaultCompositor(override val baseStream: ItemStream) : ItemCompositor.StreamCompositor {

    /**
     * 按 order 分组后的物品解释器列表
     */
    val interpreterGroups = ItemRegistry.getInterpreterInstanceGroups()

    /**
     * 物品解释器列表
     */
    val interpreters = interpreterGroups.flatten()

    /**
     * 物品源列表
     */
    val sources = ItemRegistry.sources.toList()

    /**
     * 静态物品策略
     */
    val staticStrategy = runBlocking { StaticStrategy(baseStream.fetchProperty()) }

    /**
     * 静态物品流生成器
     * replenish 在对象初始化时会生成一遍, 导致 prepare 没执行就有了补充流, 故在此先用 [lazy]
     */
    val staticGenerator by lazy { staticStrategy.StreamGenerator(this) }

    /**
     * 预处理流
     */
    override fun prepare() = runBlocking {
        runInterpreterGroups { preFlow(baseStream) }
        // 应用静态属性 (如果完全静态开启的话)
        staticGenerator.applyIfFullStatic()
    }

    /**
     * 生产新的输出流
     */
    override fun produce(): Deferred<ItemStream> = staticGenerator.streamGenerating

    /**
     * 处理物品流 (解释 -> 序列化)
     */
    override suspend fun dispatch(stream: ItemStream) = coroutineScope {
        runInterpreterGroups {
            measureTimeMillis { interpret(stream) }
                .let { debug("[TIME MARK] $this costs $it ms.") }
        }
        // 物品源处理
        SourceInterpreter.parallelInterpret(sources, stream)
    }

    override fun <T : ItemInterpreter> getInterpreter(type: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return interpreters.find { it::class.java == type } as? T
            ?: throw NoSuchElementException("Interpreter of type ${type.simpleName} not found.")
    }

    /**
     * 按阶段执行解释器组 (组内并行, 组间串行)
     */
    private suspend fun runInterpreterGroups(action: suspend ItemInterpreter.() -> Unit) = coroutineScope {
        interpreterGroups.forEach { group ->
            group.map { async { it.action() } }.awaitAll()
        }
    }

}

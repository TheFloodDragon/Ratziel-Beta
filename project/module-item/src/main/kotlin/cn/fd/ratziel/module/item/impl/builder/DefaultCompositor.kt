package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.module.item.ItemRegistry
import cn.fd.ratziel.module.item.api.builder.*
import cn.fd.ratziel.module.item.impl.builder.provided.ComponentInterpreter
import cn.fd.ratziel.module.item.impl.builder.provided.SourceInterpreter
import kotlinx.coroutines.*
import taboolib.common.platform.function.debug
import taboolib.common.reflect.hasAnnotation
import kotlin.system.measureTimeMillis

/**
 * DefaultCompositor
 *
 * @author TheFloodDragon
 * @since 2025/6/15 09:37
 */
class DefaultCompositor(override val baseStream: ItemStream) : ItemCompositor.StreamCompositor {

    /**
     * 物品解释器列表
     */
    val interpreters: List<ItemInterpreter> = ItemRegistry.getInterpreterInstances()

    /**
     * 物品源列表
     */
    val sources: List<ItemSource> = ItemRegistry.sources.toList()

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
        // 预解释 (预处理基流)
        interpreters.forEach { it.preFlow(baseStream) }
        // 应用静态属性 (如果完全静态开启的话)
        staticGenerator.applyIfFullStatic()
    }

    override fun produce(): Deferred<ItemStream> {
        return staticGenerator.streamGenerating
    }

    /**
     * 处理物品流 (解释 -> 序列化)
     */
    override suspend fun dispatch(stream: ItemStream) = coroutineScope {

        suspend fun ItemInterpreter.interpret() {
            measureTimeMillis {
                this.interpret(stream)
            }.let { t -> debug("[TIME MARK] $this costs $t ms.") }
        }

        val parallelTasks = mutableListOf<Job>()

        // 解释器处理
        for (interpreter in interpreters) {
            // 是否支持并行解释
            if (interpreter::class.java.hasAnnotation(ParallelInterpretation::class.java)) {
                // 并行解释任务
                parallelTasks += launch { interpreter.interpret() }
            } else {
                // 等待前面的并行任务完成
                parallelTasks.joinAll()
                parallelTasks.clear()
                // 串行解释任务
                interpreter.interpret()
            }
        }

        // 等待所有并行任务完成
        parallelTasks.joinAll()

        // 物品源处理
        val sourcesTask = SourceInterpreter.parallelInterpret(sources, stream)

        // 物品源任务也需要在最后完成
        sourcesTask.join()

        // 序列化任务解释 (手动调用)
        ComponentInterpreter.interpret()
    }

    override fun <T : ItemInterpreter> getInterpreter(type: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return this.interpreters.find { it::class.java == type } as? T
            ?: throw NoSuchElementException("Interpreter of type ${type.simpleName} not found.")
    }

}
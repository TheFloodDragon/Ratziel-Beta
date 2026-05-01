package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.module.item.ItemRegistry
import cn.fd.ratziel.module.item.api.builder.ItemCompositor
import cn.fd.ratziel.module.item.api.builder.ItemInterpreter
import cn.fd.ratziel.module.item.api.builder.ItemStream
import cn.fd.ratziel.module.item.impl.builder.provided.SourceInterpreter
import kotlinx.coroutines.*
import taboolib.common.platform.function.debug
import kotlin.system.measureTimeMillis

/**
 * DefaultCompositor
 *
 * @author TheFloodDragon
 * @since 2025/6/15 09:37
 */
class DefaultCompositor(baseStream: NativeItemStream) : ItemCompositor.StreamCompositor {

    override val baseStream = baseStream.apply {
        compositor = this@DefaultCompositor // 设置编排器
    }

    /**
     * 物品的解释器分组 (每个分组内的解释器可以并行执行, 分组之间串行执行)
     */
    var interpreterGroups: List<List<ItemInterpreter>> = ItemRegistry.getInterpreterInstanceGroups()
        private set(value) {
            field = value
            lookupInterpreter = value.flatten().associateBy { it::class.java } // 使其保持同步
        }

    /**
     * 快速解释器实例查找表
     */
    var lookupInterpreter: Map<Class<*>, ItemInterpreter> = emptyMap()
        private set

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
    override fun prepare() {
        val toRemove = linkedSetOf<ItemInterpreter>() // 禁用的解释器列表
        runBlocking {
            runInterpreters {
                if (!it.preFlow(baseStream)) toRemove.add(it)
            }
            // 应用静态属性 (如果完全静态开启的话)
            staticGenerator.applyIfFullStatic()
        }
        interpreterGroups = interpreterGroups.map { g -> g.filter { it !in toRemove } }
    }

    /**
     * 生产新的输出流
     */
    override fun produce(): Deferred<ItemStream> = staticGenerator.streamGenerating

    /**
     * 处理物品流 (解释 -> 序列化)
     */
    override suspend fun dispatch(stream: ItemStream) = coroutineScope {
        runInterpreters {
            measureTimeMillis { it.interpret(stream) }
                .let { t -> debug("[TIME MARK] $this costs $t ms.") }
        }
        // 物品源处理
        SourceInterpreter.parallelInterpret(ItemRegistry.sources, stream)
    }

    override fun <T : ItemInterpreter> interpreter(type: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return lookupInterpreter[type] as? T ?: throw NoSuchElementException("Interpreter of type ${type.simpleName} not found.")
    }

    /**
     * 按阶段执行解释器组 (组内并行, 组间串行)
     */
    private suspend fun runInterpreters(action: suspend (ItemInterpreter) -> Unit) = coroutineScope {
        for (group in interpreterGroups) {
            group.map {
                launch { action.invoke(it) }
            }.joinAll()
        }
    }

}

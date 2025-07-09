package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.module.item.ItemElement
import cn.fd.ratziel.module.item.ItemRegistry
import cn.fd.ratziel.module.item.api.builder.AsyncInterpretation
import cn.fd.ratziel.module.item.api.builder.InterpreterCompositor
import cn.fd.ratziel.module.item.api.builder.ItemInterpreter
import cn.fd.ratziel.module.item.api.builder.ItemStream
import cn.fd.ratziel.module.item.internal.SourceInterpreter
import kotlinx.coroutines.*
import taboolib.common.platform.function.debug
import taboolib.common.reflect.hasAnnotation
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.system.measureTimeMillis

/**
 * DefaultCompositor
 *
 * @author TheFloodDragon
 * @since 2025/6/15 09:37
 */
class DefaultCompositor(baseStream: NativeItemStream) : InterpreterCompositor {

    init {
        runBlocking(ItemElement.coroutineContext) {
            // 预先解释 (处理基流)
            ItemRegistry.interpreters.forEach {
                if (it is ItemInterpreter.PreInterpretable) it.preFlow(baseStream)
            }
        }
    }

    override suspend fun dispatch(stream: ItemStream) = coroutineScope {

        suspend fun ItemInterpreter.interpret() {
            measureTimeMillis {
                this.interpret(stream)
            }.let { t -> debug("[TIME MARK] $this costs $t ms.") }
        }

        val asyncTasks = ConcurrentLinkedQueue<Job>()

        // 解释器处理
        for (interpreter in ItemRegistry.interpreters) {
            // 是否支持异步解释
            if (interpreter::class.java.hasAnnotation(AsyncInterpretation::class.java)) {
                // 异步解释任务
                asyncTasks += launch { interpreter.interpret() }
            } else {
                // 等待前面的异步任务完成
                asyncTasks.joinAll()
                asyncTasks.clear()
                // 同步解释任务
                interpreter.interpret()
            }
        }

        // 物品源处理
        val sourceTasks = ItemRegistry.sources.map {
            async {
                SourceInterpreter(it).interpret(stream)
            }
        }

        // 等待所有任务完成
        asyncTasks.joinAll()
        val sourceResults = sourceTasks.awaitAll()

        // 源任务材质重排序
        SourceInterpreter.sequenceMaterial(stream, sourceResults)

    }

}
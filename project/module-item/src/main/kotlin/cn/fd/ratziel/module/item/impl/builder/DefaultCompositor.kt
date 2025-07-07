package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.module.item.ItemRegistry
import cn.fd.ratziel.module.item.api.builder.InterpreterCompositor
import cn.fd.ratziel.module.item.api.builder.ItemInterpreter
import cn.fd.ratziel.module.item.api.builder.ItemStream
import cn.fd.ratziel.module.item.internal.SourceInterpreter
import io.rokuko.azureflow.utils.debug
import kotlinx.coroutines.*
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

    val interpreters = ItemRegistry.interpreters.map { it.get() }

    init {
        runBlocking {
            // 预先解释 (处理基流)
            interpreters.forEach {
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
        for (interpreter in interpreters) {
            // 是否支持异步解释
            if (interpreter::class.java.hasAnnotation(ItemInterpreter.AsyncInterpretation::class.java)) {
                // 异步解释任务
                val task = launch { interpreter.interpret() }
                asyncTasks += task // 进入队列
                // 完成后跳出队列
                task.invokeOnCompletion { asyncTasks.remove(task) }
            } else {
                // 等待前面的异步任务完成
                asyncTasks.joinAll()
                // 同步解释任务
                interpreter.interpret()
            }
        }

        // 物品源处理
        val sourceTasks = ItemRegistry.sources.map {
            launch {
                SourceInterpreter(it).interpret()
            }
        }

        // 等待所有任务完成
        sourceTasks.joinAll()
        asyncTasks.joinAll()

    }

}
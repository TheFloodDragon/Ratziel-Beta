package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.module.item.ItemRegistry
import cn.fd.ratziel.module.item.api.builder.InterpreterCompositor
import cn.fd.ratziel.module.item.api.builder.ItemInterpreter
import cn.fd.ratziel.module.item.api.builder.ItemStream
import kotlinx.coroutines.*
import taboolib.common.reflect.hasAnnotation
import java.util.concurrent.ConcurrentLinkedQueue

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

        val asyncTasks = ConcurrentLinkedQueue<Job>()

        // 物品源处理
        val sourceTasks = ItemRegistry.sources.map {
            launch { it.generateItem(stream.origin, stream.context) }
        }

        // 解释器处理
        for (interpreter in interpreters) {
            // 是否支持异步解释
            if (interpreter::class.java.hasAnnotation(ItemInterpreter.AsyncInterpretation::class.java)) {
                // 异步解释任务
                val task = launch { interpreter.interpret(stream) }
                asyncTasks += task // 进入队列
                // 完成后跳出队列
                task.invokeOnCompletion { asyncTasks.remove(task) }
            } else {
                // 等待前面的异步任务完成
                asyncTasks.joinAll()
                // 同步解释任务
                interpreter.interpret(stream)
            }
        }

        // 等待所有任务完成
        sourceTasks.joinAll()
        asyncTasks.joinAll()

    }

}
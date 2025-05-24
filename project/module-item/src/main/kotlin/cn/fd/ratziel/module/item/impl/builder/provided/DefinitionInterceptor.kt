package cn.fd.ratziel.module.item.impl.builder.provided

import cn.fd.ratziel.common.element.registry.AutoRegister
import cn.fd.ratziel.common.event.ElementEvaluateEvent
import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.module.item.ItemElement
import cn.fd.ratziel.module.item.api.builder.ItemInterceptor
import cn.fd.ratziel.module.item.api.builder.ItemStream
import cn.fd.ratziel.module.item.api.builder.ItemTagResolver
import cn.fd.ratziel.module.script.block.ExecutableBlock
import cn.fd.ratziel.module.script.block.ScriptBlockBuilder
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope
import kotlinx.serialization.json.JsonObject
import taboolib.common.platform.event.SubscribeEvent
import java.util.concurrent.ConcurrentHashMap

/**
 * DefinitionInterceptor
 *
 * @author TheFloodDragon
 * @since 2025/5/10 19:49
 */
object DefinitionInterceptor : ItemInterceptor {

    /**
     * DefinitionContext
     *
     * @author TheFloodDragon
     * @since 2025/5/17 17:20
     */
    class DefinitionContext(
        val map: Map<String, Any?>,
    )

    @AutoRegister
    object DefinitionResolver : ItemTagResolver {
        override val alias = arrayOf("define", "def", "definition")
        override fun resolve(task: ItemTagResolver.ResolvationTask, context: ArgumentContext) {
            val definition = context.popOrNull(DefinitionContext::class.java) ?: return
            val value = definition.map[task.args.firstOrNull() ?: return]
            task.complete(value.toString())
        }
    }

    private val cache: MutableMap<String, Map<String, ExecutableBlock>> = ConcurrentHashMap()

    override suspend fun intercept(stream: ItemStream) {
        val element = stream.fetchElement()
        if (element !is JsonObject) return
        val id = stream.identifier.content

        // 获取语句块表
        val blocks = cache[id]
            ?: run {
                // 读取定义
                val define = element["define"] as? JsonObject ?: return@run null
                val map = buildBlockMap(define)
                // 加入到缓存
                cache[id] = map
                map
            } ?: return

        // 创建新的定义
        val result = executeAll(blocks, stream.context)

        // 等待所有任务完成, 并将 写入到环境里
        stream.context.put(DefinitionContext(result))
    }

    internal suspend fun buildBlockMap(element: JsonObject): Map<String, ExecutableBlock> = supervisorScope {
        mapOf(*element.map {
            async {
                it.key to ScriptBlockBuilder.build(it.value) // 构建语句块
            }
        }.awaitAll().toTypedArray())
    }

    internal suspend fun executeAll(blocks: Map<String, ExecutableBlock>, context: ArgumentContext): Map<String, Any?> = supervisorScope {
        mapOf(*blocks.map {
            async {
                it.key to it.value.execute(context) // 执行语句块
            }
        }.awaitAll().toTypedArray())
    }

    @SubscribeEvent
    private fun onProcess(event: ElementEvaluateEvent.Process) {
        if (event.handler !is ItemElement) return
        this.cache.remove(event.element.name)
    }

    @SubscribeEvent
    private fun onStart(event: ElementEvaluateEvent.Start) {
        if (event.handler !is ItemElement) return
        this.cache.clear()
    }

}
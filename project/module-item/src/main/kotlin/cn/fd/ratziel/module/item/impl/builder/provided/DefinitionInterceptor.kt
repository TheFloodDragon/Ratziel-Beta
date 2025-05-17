package cn.fd.ratziel.module.item.impl.builder.provided

import cn.altawk.nbt.tag.NbtTag
import cn.fd.ratziel.common.element.registry.AutoRegister
import cn.fd.ratziel.common.event.ElementEvaluateEvent
import cn.fd.ratziel.module.item.ItemElement
import cn.fd.ratziel.module.item.api.builder.ItemInterceptor
import cn.fd.ratziel.module.item.api.builder.ItemStream
import cn.fd.ratziel.module.item.impl.RatzielItem
import cn.fd.ratziel.module.item.impl.SimpleData
import cn.fd.ratziel.module.nbt.NbtAdapter
import cn.fd.ratziel.module.script.block.ExecutableBlock
import cn.fd.ratziel.module.script.block.ScriptBlockBuilder
import kotlinx.coroutines.*
import kotlinx.serialization.json.JsonObject
import taboolib.common.platform.event.SubscribeEvent
import java.util.concurrent.ConcurrentHashMap

/**
 * DefinitionInterceptor
 *
 * @author TheFloodDragon
 * @since 2025/5/10 19:49
 */
@AutoRegister
object DefinitionInterceptor : ItemInterceptor {

    /**
     * DefinitionContext
     *
     * @author TheFloodDragon
     * @since 2025/5/17 17:20
     */
    class DefinitionContext(
        val defineMap: Map<String, Any?>,
        val dataMap: Map<String, Any?>,
    )

    private val definitionCache: MutableMap<String, Map<String, ExecutableBlock>> = ConcurrentHashMap()
    private val dataCache: MutableMap<String, Map<String, ExecutableBlock>> = ConcurrentHashMap()

    override suspend fun intercept(stream: ItemStream) = coroutineScope {
        val element = stream.fetchElement()
        if (element !is JsonObject) return@coroutineScope

        // 定义处理
        val defineTask = async {
            // 获取语句块表
            val blocks = definitionCache[stream.identifier.content]
                ?: run {
                    // 读取定义
                    val define = element["define"] as? JsonObject ?: return@apply
                    val map = buildBlockMap(element)
                    // 加入到缓存
                    definitionCache[stream.identifier.content] = map
                    map
                }

            // 创建新的定义
            executeAll(blocks)
        }


        // 数据处理
        val dataTask = scope.async {
            // 获取语句块表
            val blocks = dataCache[stream.identifier.content]
                ?: run {
                    // 读取定义
                    val define = element["data"] as? JsonObject ?: return@apply
                    val map = buildBlockMap(element)
                    // 加入到缓存
                    dataCache[stream.identifier.content] = map
                    map
                }

            // 创建数据容器
            val holder = RatzielItem.Holder(SimpleData())

            // 创建新的定义
            val result = executeAll(blocks)

            for ((name, value) in result) {
                // 处理 Nbt 数据
                val tag = (value ?: continue) as? NbtTag
                    ?: runCatching { NbtAdapter.box(value) }.getOrNull() ?: continue
                // 扔到数据容器中
                holder[name] = tag
            }

            // 写入数据
            stream.data.withValue {
                it.tag.merge(holder.data.tag, true)
            }

            return@async result
        }

        // 等待所有任务完成, 并将 写入到环境里
        val definition = DefinitionContext(
            defineTask.await(),
            dataTask.await()
        )
        stream.context.put(definition)
    }

    suspend fun buildBlockMap(element: JsonObject): Map<String, ExecutableBlock> = supervisorScope {
        mapOf(element.map {
            async {
                // 构建语句块
                it.key to ScriptBlockBuilder.build(it.value)
            }
        }.awaitAll()))
    }

    suspend fun executeAll(map: Map<String, ExecutableBlock>): Map<String, Any?> = supervisorScope {
        mapOf(blocks.map {
            async {
                it.key to it.value.execute()
            }
        }.awaitAll())
    }

    @SubscribeEvent
    fun onProcess(event: ElementEvaluateEvent.Process) {
        if (event.handler !is ItemElement) return
        definitionCache.remove(event.element.name)
        dataCache.remove(event.element.name)
    }

    @SubscribeEvent
    fun onStart(event: ElementEvaluateEvent.Start) {
        if (event.handler !is ItemElement) return
        definitionCache.clear()
        dataCache.clear()
    }

}
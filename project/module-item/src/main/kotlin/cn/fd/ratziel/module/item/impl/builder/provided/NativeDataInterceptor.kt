package cn.fd.ratziel.module.item.impl.builder.provided

import cn.fd.ratziel.common.element.registry.AutoRegister
import cn.fd.ratziel.common.event.ElementEvaluateEvent
import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.module.item.ItemElement
import cn.fd.ratziel.module.item.api.builder.ItemInterceptor
import cn.fd.ratziel.module.item.api.builder.ItemStream
import cn.fd.ratziel.module.item.api.builder.ItemTagResolver
import cn.fd.ratziel.module.item.impl.RatzielItem
import cn.fd.ratziel.module.nbt.NbtAdapter
import cn.fd.ratziel.module.script.block.ExecutableBlock
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonObject
import taboolib.common.platform.event.SubscribeEvent
import java.util.concurrent.ConcurrentHashMap

/**
 * NativeDataInterceptor
 *
 * @author TheFloodDragon
 * @since 2025/5/24 18:21
 */
object NativeDataInterceptor : ItemInterceptor {

    @AutoRegister
    object NativeDataResolver : ItemTagResolver {
        override val alias = arrayOf("data")
        override fun resolve(assignment: ItemTagResolver.Assignment, context: ArgumentContext) {
            // 获取物品流
            val stream = context.popOrNull(ItemStream::class.java) ?: return
            // 数据名称
            val name = assignment.args.firstOrNull() ?: return
            // 获取数据
            runBlocking {
                stream.data.withValue {
                    // 创建 Holder 以获取数据
                    val holder = RatzielItem.Holder(it)
                    val value = holder[name] ?: return@withValue
                    // 结束解析
                    assignment.complete(value.content.toString())
                }
            }
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
                // 读取数据
                val define = element["data"] as? JsonObject ?: return@run null
                val map = DefinitionInterceptor.buildBlockMap(define)
                // 加入到缓存
                cache[id] = map
                map
            } ?: return

        val result = DefinitionInterceptor.executeAll(blocks, stream.context)
            .mapValues { (_, value) ->
                runCatching {
                    value?.let { NbtAdapter.box(it) }
                }.getOrNull()
            }

        // 写入到物品数据里
        stream.data.withValue {
            // 创建 Holder 以写入数据
            val holder = RatzielItem.Holder(it)
            // 写入数据
            for ((key, value) in result) {
                holder[key] = value ?: continue
            }
        }
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
package cn.fd.ratziel.module.item.impl.builder.provided

import cn.fd.ratziel.common.event.ElementEvaluateEvent
import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.module.item.ItemElement
import cn.fd.ratziel.module.item.api.DataHolder
import cn.fd.ratziel.module.item.api.builder.ItemInterceptor
import cn.fd.ratziel.module.item.api.builder.ItemStream
import cn.fd.ratziel.module.item.api.builder.ItemTagResolver
import cn.fd.ratziel.module.item.impl.RatzielItem
import cn.fd.ratziel.module.item.impl.builder.NativeItemStream
import cn.fd.ratziel.module.item.impl.builder.TaggedSectionResolver
import cn.fd.ratziel.module.item.impl.feature.dynamic.DynamicTagService
import cn.fd.ratziel.module.script.block.ExecutableBlock
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

    /**
     * 原生数据解析器
     * 因为从中使用了 [RatzielItem], 故需要手动控制
     */
    object NativeDataResolver : ItemTagResolver {
        override val alias = arrayOf("data")
        override fun resolve(assignment: ItemTagResolver.Assignment, context: ArgumentContext) {
            // 数据名称
            val name = assignment.args.firstOrNull() ?: return
            // 获取物品 Holder
            val holder = context.popOrNull(DataHolder::class.java) ?: return
            // 获取数据
            val value = holder[name] ?: return
            // 结束解析
            assignment.complete(value.toString())
        }
    }

    init {
        // 支持动态标签解析
        DynamicTagService.registerResolver(NativeDataResolver)
    }

    private val cache: MutableMap<String, Map<String, ExecutableBlock>> = ConcurrentHashMap()

    override suspend fun intercept(stream: ItemStream) {
        // 需求 NativeItemStream
        if (stream !is NativeItemStream) return

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

        // 写入到物品数据里
        stream.data.withValue {
            // 创建 Holder 以写入数据
            val holder = RatzielItem.Holder(it)
            // 写入数据
            for ((key, value) in result) {
                holder[key] = value ?: continue
            }
        }

        // 标签解析 (物品数据虽然没有使用到, 但仍需拿锁, 确保NativeDataResolver和其他东西的线程安全)
        stream.tree.togetherWith(stream.data) { tree, _ ->
            // 将物品放入上下文
            stream.context.put(stream.item)
            // 执行标签解析
            TaggedSectionResolver.resolveWithSingle(
                NativeDataResolver, tree, stream.context
            )
            // 清理物品上下文
            stream.context.remove(RatzielItem::class.java)
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
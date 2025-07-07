package cn.fd.ratziel.module.item.impl.builder.provided

import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.module.item.api.DataHolder
import cn.fd.ratziel.module.item.api.builder.ItemInterpreter
import cn.fd.ratziel.module.item.api.builder.ItemStream
import cn.fd.ratziel.module.item.api.builder.ItemTagResolver
import cn.fd.ratziel.module.item.impl.RatzielItem
import cn.fd.ratziel.module.item.impl.builder.NativeItemStream
import cn.fd.ratziel.module.item.impl.builder.TaggedSectionResolver
import cn.fd.ratziel.module.script.block.ExecutableBlock
import kotlinx.serialization.json.JsonObject

/**
 * NativeDataInterpreter
 *
 * @author TheFloodDragon
 * @since 2025/5/24 18:21
 */
@ItemInterpreter.AsyncInterpretation
class NativeDataInterpreter : ItemInterpreter.PreInterpretable {

    /**
     * 原生数据解析器
     * 因为从中使用了 [RatzielItem], 故需要手动控制
     */
    object NativeDataResolver : ItemTagResolver {
        override val alias = arrayOf("data")
        override fun resolve(args: List<String>, context: ArgumentContext): String? {
            // 数据名称
            val name = args.firstOrNull() ?: return null
            // 获取物品 Holder
            val holder = context.popOrNull(DataHolder::class.java) ?: return null
            // 获取数据 (若找不到则找第二个参数, 第二个参数也没有就返回)
            val value = holder[name] ?: args.getOrNull(1) ?: return null
            // 结束解析
            return value.toString()
        }
    }

    private lateinit var cachedBlocks: Map<String, ExecutableBlock>

    override suspend fun preFlow(stream: ItemStream) {
        val element = stream.fetchElement()
        if (element !is JsonObject) return

        // 读取数据
        val define = element["data"] as? JsonObject ?: return
        val map = DefinitionInterpreter.buildBlockMap(define)
        // 加入到缓存
        this.cachedBlocks = map
    }

    override suspend fun interpret(stream: ItemStream) {
        // 需求 NativeItemStream
        if (stream !is NativeItemStream) return

        // 获取语句块表
        val blocks = if (::cachedBlocks.isInitialized) cachedBlocks else return

        val result = DefinitionInterpreter.executeAll(blocks, stream.context)

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
            val context = stream.context
            context.put(stream.item)
            // 执行标签解析
            TaggedSectionResolver.resolveWithSingle(
                NativeDataResolver, tree, context
            )
            // 清理物品上下文
            context.remove(RatzielItem::class.java)
        }

    }

}
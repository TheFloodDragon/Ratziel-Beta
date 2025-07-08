package cn.fd.ratziel.module.item.impl.builder.provided

import cn.fd.ratziel.core.functional.ArgumentContext
import cn.fd.ratziel.core.functional.CacheContext
import cn.fd.ratziel.module.item.api.builder.AsyncInterpretation
import cn.fd.ratziel.module.item.api.builder.ItemInterpreter
import cn.fd.ratziel.module.item.api.builder.ItemStream
import cn.fd.ratziel.module.item.api.builder.ItemTagResolver
import cn.fd.ratziel.module.item.impl.action.ActionManager.trigger
import cn.fd.ratziel.module.item.impl.action.registerTrigger
import cn.fd.ratziel.module.item.impl.builder.NativeItemStream
import cn.fd.ratziel.module.item.impl.builder.TaggedSectionResolver
import cn.fd.ratziel.module.script.block.BlockBuilder
import cn.fd.ratziel.module.script.block.ExecutableBlock
import cn.fd.ratziel.module.script.impl.VariablesMap
import cn.fd.ratziel.module.script.util.varsMap
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.json.JsonObject

/**
 * DefinitionInterpreter
 *
 * @author TheFloodDragon
 * @since 2025/5/10 19:49
 */
@AsyncInterpretation
object DefinitionInterpreter : ItemInterpreter.PreInterpretable {

    /** 处理触发器 **/
    val PROCESS_TRIGGER = registerTrigger("onProcess", "process")

    object DefinitionResolver : ItemTagResolver {
        override val alias = arrayOf("define", "def", "definition")
        override fun resolve(args: List<String>, context: ArgumentContext): String? {
            val vars = context.popOrNull(VariablesMap::class.java) ?: return null
            val value = vars[args.firstOrNull() ?: return null]
            return value.toString()
        }
    }

    private val blocksCacher = CacheContext.Catcher<Map<String, ExecutableBlock>>(this) { emptyMap() }

    override suspend fun preFlow(stream: ItemStream) {
        val element = stream.fetchElement()
        if (element !is JsonObject) return
        // 读取定义
        val define = element["define"] as? JsonObject ?: return
        val map = buildBlockMap(define)
        // 加入到缓存
        blocksCacher.setCache(stream.context, map)
    }

    override suspend fun interpret(stream: ItemStream) {
        val blocks = blocksCacher.catch(stream.context)

        // 获取变量表
        val vars = stream.context.varsMap()
        vars.putAll(executeAll(blocks, stream.context))

        // 等待所有任务完成, 并将 写入到环境里
        stream.data.withValue { _ ->
            // 替换定义标签
            stream.tree.withValue { tree ->
                TaggedSectionResolver.resolveWithSingle(DefinitionResolver, tree, stream.context)
            }
            // 触发触发器
            PROCESS_TRIGGER.trigger(stream.identifier) {
                // 导入变量表
                bindings.putAll(vars)
                // 尝试获取 RatzielItem 物品
                set("item", (stream as? NativeItemStream)?.item)
            }
        }
    }

    internal suspend fun buildBlockMap(element: JsonObject): Map<String, ExecutableBlock> = coroutineScope {
        mapOf(*element.map {
            async {
                it.key to BlockBuilder.build(it.value) // 构建语句块
            }
        }.awaitAll().toTypedArray())
    }

    internal suspend fun executeAll(blocks: Map<String, ExecutableBlock>, context: ArgumentContext): Map<String, Any?> = coroutineScope {
        mapOf(*blocks.map {
            async {
                it.key to it.value.execute(context) // 执行语句块
            }
        }.awaitAll().toTypedArray())
    }

}
package cn.fd.ratziel.module.item.impl.builder.provided

import cn.fd.ratziel.common.block.BlockBuilder
import cn.fd.ratziel.common.block.ExecutableBlock
import cn.fd.ratziel.core.contextual.ArgumentContext
import cn.fd.ratziel.core.contextual.plus
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.util.getBy
import cn.fd.ratziel.module.item.ItemManager
import cn.fd.ratziel.module.item.api.DataHolder
import cn.fd.ratziel.module.item.api.IdentifiedItem
import cn.fd.ratziel.module.item.api.builder.ItemInterpreter
import cn.fd.ratziel.module.item.api.builder.ItemStream
import cn.fd.ratziel.module.item.api.builder.ItemTagResolver
import cn.fd.ratziel.module.item.api.builder.ParallelInterpretation
import cn.fd.ratziel.module.item.feature.action.ActionManager
import cn.fd.ratziel.module.item.feature.action.ActionManager.trigger
import cn.fd.ratziel.module.item.impl.builder.DefaultResolver
import cn.fd.ratziel.module.item.impl.builder.NativeItemStream
import cn.fd.ratziel.module.script.util.varsMap
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import java.util.concurrent.ConcurrentHashMap

/**
 * DataInterpreter - 数据解释器
 *
 * 参考了 AzureFlow 的数据层设计.
 *
 * 而 [DataInterpreter] 总共分三层:
 * - PROPERTIES 常量层 (或称属性层): 在预解释阶段完成.
 * - DATA 数据层: 在物品生成时完成并将数据存储到 [DataHolder] 中.
 * - COMPUTATION 计算层: 前面两层完成后, 在物品生成时进行计算变量.
 *
 * 先后顺序: PROPERTIES -> DATA -> COMPUTATION.
 *
 * @author TheFloodDragon
 * @since 2025/7/24 11:44
 */
@ParallelInterpretation
class DataInterpreter : ItemInterpreter {

    /**
     * 常量层属性数据
     */
    val properties: MutableMap<String, Any> = ConcurrentHashMap()

    /**
     * 数据层脚本缓存
     */
    val dataBlocks: MutableMap<String, ExecutableBlock> = ConcurrentHashMap()

    /**
     * 计算层脚本缓存
     */
    val computationBlocks: MutableMap<String, ExecutableBlock> = ConcurrentHashMap()

    override suspend fun preFlow(stream: ItemStream): Unit = coroutineScope {
        val element = stream.fetchElement()

        // 常量层
        launch {
            // 构建语句块
            val blocks = buildBlocks(element, PROPERTIES_ALIAS) ?: return@launch
            // 执行所有语句块
            val results = executeBlocks(blocks, stream.context)
            // 将结果存入常量层属性数据
            results.forEach { (k, v) -> if (v != null) properties[k] = v }
        }

        // 数据层
        launch {
            // 构建语句块
            val blocks = buildBlocks(element, DATA_ALIAS) ?: return@launch
            // 将语句块存入数据层脚本缓存
            dataBlocks.putAll(blocks)
        }

        // 计算层
        launch {
            // 构建语句块
            val blocks = buildBlocks(element, COMPUTATION_ALIAS) ?: return@launch
            // 将语句块存入计算层脚本缓存
            computationBlocks.putAll(blocks)
        }

    }

    override suspend fun interpret(stream: ItemStream) {
        // 获取上下文变量表
        val vars = stream.context.varsMap()

        // 导入常量层属性数据
        vars.putAll(properties)
        // 执行数据层语句块, 导入数据层数据
        val dataResults = executeBlocks(dataBlocks, stream.context)
        vars.putValues(dataResults)
        // 执行计算层语句块, 导入计算层数据
        vars.putValues(executeBlocks(computationBlocks, stream.context))

        // 计算层标签处理 (实际上此标签处理的过程中可以获取到三个层的数据)
        stream.tree.withValue { tree ->
            DefaultResolver.resolveBy(ComputationResolver, tree, stream.context)
        }

        // 处理物品数据
        if (stream is NativeItemStream) {
            stream.data.withValue { _ -> // 这里只有占用锁的用处

                // 数据层处理
                if (stream.item is DataHolder) {
                    val holder = stream.item
                    // 将数据层结果写入到物品数据中
                    for ((key, value) in dataResults) {
                        holder[key] = value ?: continue
                    }
                    // 数据层标签解析
                    stream.tree.withValue { tree ->
                        // 执行标签解析
                        DefaultResolver.resolveBy(
                            NativeDataResolver, tree,
                            stream.context.plus(holder) // 加了物品的副本
                        )
                    }
                }

                // 触发触发器
                POST_TRIGGER.trigger(stream.identifier) {
                    // 导入变量表
                    putAll(vars)
                    // 尝试获取 RatzielItem 物品
                    set("item", stream.item)
                }

            }
        }

    }

    /**
     * 数据层标签解析器 - 由 [DataInterpreter] 管理 (支持动态解析)
     */
    object NativeDataResolver : ItemTagResolver {
        override val alias = DATA_ALIAS
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

    /**
     * 计算层标签解析器 - 由 [DataInterpreter] 管理 (支持动态解析)
     */
    object ComputationResolver : ItemTagResolver {
        override val alias = COMPUTATION_ALIAS + PROPERTIES_ALIAS
        override fun resolve(args: List<String>, context: ArgumentContext): String? {
            val key = args.firstOrNull() ?: return null
            val vars = context.varsMap()

            if (vars.isEmpty() || !vars.containsKey(key)) {
                // 如果变量表中没有这个键, 则尝试从物品中获取
                val item = context.popOrNull(IdentifiedItem::class.java) ?: return null
                // 从注册表中取
                val interpreter = ItemManager.registry[item.identifier.content]?.compositor
                    ?.getInterpreter(DataInterpreter::class.java) ?: return null
                // 可以获取属性的或者计算值
                return when {
                    interpreter.properties.containsKey(key) -> interpreter.properties[key].toString()
                    interpreter.computationBlocks.containsKey(key) -> interpreter.computationBlocks[key]?.execute(context).toString()
                    else -> null
                }
            } else {
                return vars[key].toString()
            }
        }
    }

    companion object {

        /**
         * 数据处理完成触发器 (在 [DataInterpreter] 数据处理完后触发)
         */
        @JvmField
        val POST_TRIGGER = ActionManager.registerSimple("onPost", "post")

        /**
         * 常量层属性定义域别名
         */
        @JvmField
        val PROPERTIES_ALIAS = arrayOf("props", "consts")

        /**
         * 数据层定义域别名
         */
        @JvmField
        val DATA_ALIAS = arrayOf("data")

        /**
         * 计算层定义域别名
         */
        @JvmField
        val COMPUTATION_ALIAS = arrayOf("compute", "computed", "define")

        /**
         * 构建语句块表
         */
        private suspend fun buildBlocks(element: Element, alias: Array<String>): Map<String, ExecutableBlock>? {
            val property = (element.property as? JsonObject)
                ?.getBy(*alias) as? JsonObject ?: return null
            return coroutineScope {
                property.mapValues {
                    async { BlockBuilder.build(element.copyOf(it.value)) }
                }.mapValues { it.value.await() }
            }
        }

        /**
         * 执行语句块
         * @return 执行结果
         */
        private suspend fun executeBlocks(blocks: Map<String, ExecutableBlock>, context: ArgumentContext): Map<String, Any?> = coroutineScope {
            blocks.mapValues {
                async { it.value.execute(context) }
            }.mapValues { it.value.await() }
        }

    }

}
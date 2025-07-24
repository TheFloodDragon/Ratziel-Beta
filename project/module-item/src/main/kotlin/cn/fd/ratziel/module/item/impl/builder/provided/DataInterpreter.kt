package cn.fd.ratziel.module.item.impl.builder.provided

import cn.fd.ratziel.core.functional.ArgumentContext
import cn.fd.ratziel.core.serialization.json.getBy
import cn.fd.ratziel.module.item.api.DataHolder
import cn.fd.ratziel.module.item.api.builder.ItemInterpreter
import cn.fd.ratziel.module.item.api.builder.ItemStream
import cn.fd.ratziel.module.item.api.builder.ItemTagResolver
import cn.fd.ratziel.module.item.impl.action.registerTrigger
import cn.fd.ratziel.module.script.block.BlockBuilder
import cn.fd.ratziel.module.script.block.ExecutableBlock
import cn.fd.ratziel.module.script.impl.VariablesMap
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
class DataInterpreter : ItemInterpreter {

    /**
     * 常量层属性数据
     */
    val properties: VariablesMap = VariablesMap(ConcurrentHashMap())

    /**
     * 数据层脚本缓存
     */
    val dataBlocks: MutableMap<String, ExecutableBlock> = ConcurrentHashMap()

    /**
     * 计算层脚本缓存
     */
    val computationBlocks: MutableMap<String, ExecutableBlock> = ConcurrentHashMap()

    override suspend fun preFlow(stream: ItemStream) = coroutineScope {
        val element = stream.fetchElement()
        if (element !is JsonObject) return@coroutineScope

        // 常量层
        launch {
            // 构建语句块
            val blocks = buildBlocks(element, PROPERTIES_ALIAS) ?: return@launch
            // 执行所有语句块
            val results = executeBlocks(blocks, stream.context)
            // 将结果存入常量层属性数据
            properties.putAll(results)
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
        TODO()
    }

    /**
     * 数据层标签解析器 - 由 [DataInterpreter] 管理
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
     * 计算层标签解析器 - 由 [DataInterpreter] 管理
     */
    object ComputationResolver : ItemTagResolver {
        override val alias = COMPUTATION_ALIAS
        override fun resolve(args: List<String>, context: ArgumentContext): String? {
            val vars = context.popOrNull(VariablesMap::class.java) ?: return null
            val value = vars[args.firstOrNull() ?: return null]
            return value.toString()
        }
    }

    companion object {

        /**
         * 数据处理触发器 (在 [DataInterpreter] 数据处理完后触发)
         */
        @JvmField
        val PROCESS_TRIGGER = registerTrigger("onProcess", "process")

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
        private suspend fun buildBlocks(element: JsonObject, alias: Array<String>): Map<String, ExecutableBlock>? {
            return buildBlocks(element.getBy(*alias) as? JsonObject ?: return null)
        }

        /**
         * 构建语句块
         */
        private suspend fun buildBlocks(element: JsonObject): Map<String, ExecutableBlock> = coroutineScope {
            element.mapValues {
                async { BlockBuilder.build(it.value) }
            }.mapValues { it.value.await() }
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
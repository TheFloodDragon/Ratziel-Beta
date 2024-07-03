package cn.fd.ratziel.module.item.impl.builder.resolver

import cn.fd.ratziel.core.Priority
import cn.fd.ratziel.core.serialization.handlePrimitives
import cn.fd.ratziel.core.util.priority
import cn.fd.ratziel.core.util.sortPriority
import cn.fd.ratziel.core.util.splitNonEscaped
import cn.fd.ratziel.function.argument.ArgumentContext
import cn.fd.ratziel.module.item.api.builder.ItemResolver
import cn.fd.ratziel.module.item.impl.builder.DefaultItemSerializer
import cn.fd.ratziel.module.item.impl.builder.resolver.sectionResolvers.PapiResolver
import cn.fd.ratziel.module.item.impl.builder.resolver.sectionResolvers.RandomResolver
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import taboolib.common.util.VariableReader

/**
 * BasicItemResolver - 基础解析器
 *
 * 解析 [SectionTagResolver] 和玩家变量
 *
 * @author TheFloodDragon
 * @since 2024/5/24 21:32
 */
object BasicItemResolver : ItemResolver {

    val accessibleNodes: MutableSet<String> = DefaultItemSerializer.occupiedNodes.toMutableSet()

    /**
     * 字符串解析器
     */
    val resolvers: MutableList<Priority<SectionStringResolver>> = mutableListOf(
        PapiResolver priority -1,
        BasicTagResolver priority 99,
    )

    override fun resolve(element: JsonElement, context: ArgumentContext): JsonElement =
        // 过滤节点
        CleanUpUtil.handleOnFilter(element, accessibleNodes) { filtered ->
            // 处理 JsonPrimitive
            filtered.value.handlePrimitives { resolvePrimitive(it, context) }
        }

    fun resolvePrimitive(element: JsonPrimitive, context: ArgumentContext): JsonElement {
        var handle = element.content
        // 遍历字符串解析器处理
        for (resolver in resolvers.sortPriority()) {
            handle = resolver.resolve(handle, context)
        }
        return JsonPrimitive(handle)
    }

    object BasicTagResolver : SectionStringResolver {

        /**
         * 标签解析器
         */
        val resolvers: MutableList<SectionTagResolver> = mutableListOf(
            RandomResolver
        )

        /**
         * 参数分割符
         */
        const val ARGUMENT_SEPRATION_SIGN = ":"

        /**
         * 变量读取器
         */
        val reader = VariableReader("{", "}")

        override fun resolve(element: String, context: ArgumentContext): String =
            reader.readToFlatten(element).joinToString("") {
                // 如果是标签, 则通过标签解析器解析
                if (it.isVariable) {
                    handle(it.text, context)
                } else it.text // 不然就是它本身
            } // 拼接结果成字符串并返回

        /**
         * 寻找 [SectionTagResolver] 并处理
         */
        fun handle(source: String, context: ArgumentContext): String {
            // 分割
            val split = source.splitNonEscaped(ARGUMENT_SEPRATION_SIGN)
            // 获取名称
            val name = split.firstOrNull() ?: return source
            // 获取解析器
            val resolver = resolvers.find { it.name == name || it.alias.contains(name) }
            // 解析并返回
            return resolver?.resolve(split.drop(1), context) ?: (reader.start + source + reader.end)
        }

    }

}
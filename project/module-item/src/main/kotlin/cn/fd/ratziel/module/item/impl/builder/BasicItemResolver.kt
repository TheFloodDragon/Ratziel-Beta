package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.core.serialization.MutableJsonObject
import cn.fd.ratziel.core.serialization.handlePrimitives
import cn.fd.ratziel.core.util.splitNonEscaped
import cn.fd.ratziel.function.argument.ArgumentFactory
import cn.fd.ratziel.function.argument.popOrNull
import cn.fd.ratziel.module.item.api.builder.ItemResolver
import cn.fd.ratziel.module.item.api.builder.ItemSectionResolver
import cn.fd.ratziel.module.item.impl.builder.tagResolvers.RandomResolver
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.bukkit.OfflinePlayer
import taboolib.common.util.VariableReader
import taboolib.platform.compat.replacePlaceholder
import java.util.function.Function

/**
 * BasicItemResolver - 基础解析器
 *
 * 解析 [ItemSectionResolver.TagResolver] 和玩家变量
 *
 * @author TheFloodDragon
 * @since 2024/5/24 21:32
 */
object BasicItemResolver : ItemSectionResolver {

    object CleanUp : ItemResolver {

        /**
         * 允许通过(有用的)的节点
         */
        @JvmStatic
        val accessedNodes: MutableSet<String> = DefaultItemSerializer.occupiedNodes.toMutableSet()

        /**
         * 过滤掉无用的节点 (节点不在[accessedNodes]内的)
         */
        @JvmStatic
        @JvmOverloads
        fun cleanUp(element: JsonElement, action: Function<JsonElement, JsonElement> = Function { it }): JsonElement {
            if (element is JsonObject) {
                val newJson = MutableJsonObject()
                for (entry in element) {
                    if (entry.key in accessedNodes) newJson[entry.key] = action.apply(entry.value)
                }
                return newJson.asImmutable()
            } else return element
        }

        override fun resolve(element: JsonElement, arguments: ArgumentFactory) = cleanUp(element)

    }

    /**
     * 标签解析器
     */
    val tagResolvers: MutableList<ItemSectionResolver.TagResolver> = mutableListOf(
        RandomResolver
    )

    override fun resolveWith(builder: MutableJsonObject, arguments: ArgumentFactory): JsonElement {
        for (entry in builder) {
            if (entry.key in CleanUp.accessedNodes) {
                builder[entry.key] = resolveWith(entry.value, arguments)
            }
        }
        return builder.asImmutable()
    }

    override fun resolveWith(element: JsonElement, arguments: ArgumentFactory) = element.handlePrimitives { json ->
        var handle = json.content
        // 获取玩家
        val player = arguments.popOrNull<OfflinePlayer>()?.value
        // 处理Papi变量
        if (player != null) handle = handle.replacePlaceholder(player)
        // 处理标签
        val result = reader.readToFlatten(handle).map {
            // 如果是标签, 则通过标签解析器解析
            if (it.isVariable) {
                handle(it.text, arguments)
            } else it.text // 不然就是它本身
        }
        JsonPrimitive(result.joinToString("")) // 拼接结果成字符串并返回
    }

    /**
     * 寻找 [ItemSectionResolver.TagResolver] 并处理
     */
    fun handle(source: String, arguments: ArgumentFactory): String {
        // 分割
        val split = source.splitNonEscaped(ARGUMENT_SEPRATION_SIGN)
        // 获取名称
        val name = split.firstOrNull() ?: return source
        // 获取解析器
        val resolver = tagResolvers.find { it.name == name || it.alias.contains(name) }
        // 解析并返回
        return resolver?.resolve(split.drop(1), arguments) ?: source
    }

    /**
     * 参数分割符
     */
    const val ARGUMENT_SEPRATION_SIGN = ":"

    /**
     * 变量读取器
     */
    val reader = VariableReader("{", "}")

}
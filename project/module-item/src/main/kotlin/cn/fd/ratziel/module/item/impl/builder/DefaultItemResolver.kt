package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.core.serialization.MutableJsonObject
import cn.fd.ratziel.core.serialization.handlePrimitives
import cn.fd.ratziel.core.util.splitNonEscaped
import cn.fd.ratziel.function.argument.ArgumentFactory
import cn.fd.ratziel.function.argument.popOrNull
import cn.fd.ratziel.module.item.api.builder.ItemResolver
import cn.fd.ratziel.module.item.api.common.NamedStringResolver
import cn.fd.ratziel.module.item.api.common.StringResolver
import cn.fd.ratziel.module.item.impl.builder.resolver.RandomResolver
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.bukkit.OfflinePlayer
import taboolib.common.util.VariableReader
import taboolib.platform.compat.replacePlaceholder

/**
 * DefaultItemResolver
 *
 * @author TheFloodDragon
 * @since 2024/4/20 10:03
 */
object DefaultItemResolver : ItemResolver {

    /**
     * 参数分割符
     */
    const val ARGUMENT_SEPRATION_SIGN = ":"

    /**
     * 变量读取器
     */
    val reader = VariableReader("{", "}")

    /**
     * 字符串解析器 (解析器名称-解析器)
     */
    val resolvers = mutableListOf<NamedStringResolver>(
        RandomResolver
    )

    /**
     * 解析:
     *   过滤 -> 字符串处理
     */
    override fun resolve(element: JsonElement, arguments: ArgumentFactory): JsonElement {
        // 过滤该处理的
        val filtered = filter(element)
        // 处理所有字符串
        val resolved = handleStrings(filtered, arguments)
        // 返回结果
        return resolved
    }

    /**
     * 处理所有字符串
     */
    fun handleStrings(json: JsonElement, arguments: ArgumentFactory) = json.handlePrimitives { p ->
        p.takeIf { it.isString }?.content?.let { source ->
            var handle = source
            // 获取玩家
            val player = arguments.popOrNull<OfflinePlayer>()?.value
            // 处理PAPI变量
            if (player != null) handle = handle.replacePlaceholder(player)
            // 处理变量
            val result = reader.readToFlatten(handle).map {
                // 如果是变量, 则通过字符串解析器解析
                if (it.isVariable) {
                    distribute(it.text, arguments)
                } else it.text // 不然就是它本身
            }
            JsonPrimitive(result.joinToString("")) // 拼接结果成字符串并返回
        } ?: p
    }

    /**
     * 分配 [StringResolver]
     */
    fun distribute(source: String, arguments: ArgumentFactory): String {
        // 分割
        val split = source.splitNonEscaped(ARGUMENT_SEPRATION_SIGN)
        val name = split.firstOrNull()
        // 获取解析器
        val resolver = resolvers.find { it.name == name || it.alias.contains(name) }
        // 解析并返回
        return resolver?.resolve(split.drop(1), arguments) ?: source
    }

    /**
     * 判断此节点是否应该被解析
     */
    fun isResolvable(node: String) = DefaultItemSerializer.occupiedNodes.contains(node)

    /**
     * 初步过滤掉无用内容
     */
    fun filter(element: JsonElement) = access(element) { origin ->
        for (entry in origin) {
            // 过滤, 放入应该被处理的
            if (isResolvable(entry.key)) {
                put(entry.key, entry.value)
            }
        }
    }

    fun access(target: JsonElement, action: MutableJsonObject.(JsonObject) -> Unit): JsonElement {
        if (target is JsonObject) {
            val map = MutableJsonObject()
            action(map, target)
            return JsonObject(map)
        } else return target
    }

}
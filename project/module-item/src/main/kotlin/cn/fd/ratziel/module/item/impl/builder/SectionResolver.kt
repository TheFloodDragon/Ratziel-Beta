package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.core.exception.ArgumentNotFoundException
import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.core.serialization.json.JsonTree
import cn.fd.ratziel.core.util.splitNonEscaped
import cn.fd.ratziel.module.item.api.builder.ItemResolver
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import taboolib.common.platform.function.severe
import taboolib.common.platform.function.warning
import taboolib.common.util.VariableReader
import java.util.concurrent.ConcurrentHashMap

/**
 * SectionResolver
 *
 * @author TheFloodDragon
 * @since 2025/5/3 19:34
 */
object SectionResolver : ItemResolver {

    /**
     * 标签解析器列表
     */
    val tagResolvers: MutableMap<String, SectionTagResolver> = ConcurrentHashMap()

    /** 标签读取器 **/
    private val reader = VariableReader("{", "}")

    /** 标签参数分隔符 **/
    private const val TAG_ARG_SEPARATION = ":"

    override fun resolve(node: JsonTree.Node, context: ArgumentContext) {
        // 解析字符串
        if (node is JsonTree.PrimitiveNode && node.value.isString) {
            val resolved = resolveString(node.value.contentOrNull ?: return, node, context)
            node.value = JsonPrimitive(resolved)
        }
    }

    /**
     * 解析字符串
     */
    private fun resolveString(element: String, node: JsonTree.PrimitiveNode, context: ArgumentContext): String {
        // 读取标签, 拼接字符串片段并返回
        return reader.readToFlatten(element).joinToString("") {
            // 如果标签片段
            if (it.isVariable) {
                // 解析标签
                val handled = resolveTag(it.text, node, context)
                // 处理无误时返回处理结果, 反则返回完整标签片段
                handled ?: (reader.start + it.text + reader.end)
            } else it.text // 原文本
        }
    }

    /**
     * 从标签字符串中获取 [SectionTagResolver] 并解析
     * @return 若标签不合法或者处理后结果为空, 则返回空
     */
    private fun resolveTag(tag: String, node: JsonTree.PrimitiveNode, context: ArgumentContext): String? {
        // 分割
        val split = tag.splitNonEscaped(TAG_ARG_SEPARATION)
        // 获取名称
        val name = split.firstOrNull() ?: return null
        // 获取解析器
        val resolver = tagResolvers[name] ?: return null
        // 解析并返回
        return try {
            resolver.resolve(split.drop(1), node, context)
        } catch (ex: ArgumentNotFoundException) {
            warning("Missing argument '${ex.missingType.simpleName}' for $resolver")
            return null
        } catch (ex: Exception) {
            severe("Failed to resolve element by $resolver!")
            ex.printStackTrace()
            return null
        }
    }

}
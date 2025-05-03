package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.core.exception.ArgumentNotFoundException
import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.core.serialization.json.JsonTree
import cn.fd.ratziel.core.util.splitNonEscaped
import cn.fd.ratziel.module.item.api.builder.ItemSectionResolver
import cn.fd.ratziel.platform.bukkit.util.player
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import taboolib.common.platform.function.severe
import taboolib.common.platform.function.warning
import taboolib.common.util.VariableReader
import taboolib.platform.compat.replacePlaceholder

/**
 * SectionResolver
 *
 * @author TheFloodDragon
 * @since 2025/5/3 19:34
 */
object SectionResolver : ItemSectionResolver {

    fun resolve(element: JsonElement, context: ArgumentContext): JsonElement {
        val tree = JsonTree(element)
        JsonTree.unfold(tree.root) {
            resolve(it, context)
        }
        return tree.toElement()
    }

    fun resolve(element: JsonElement, availableNodes: Iterable<String>, context: ArgumentContext): JsonElement {
        return if (element is JsonObject) buildJsonObject {
            for ((node, value) in element) {
                val handled = if (node in availableNodes) {
                    resolve(value, context)
                } else value
                put(node, handled)
            }
        } else element
    }


    override fun resolve(node: JsonTree.Node, context: ArgumentContext) {
        if (node is JsonTree.PrimitiveNode && node.value.isString) {
            resolveString(node.value.contentOrNull ?: return, context)
        }
    }

    /**
     * 匹配 [SectionTagResolver]
     * @return 匹配不到时返回空
     */
    fun match(name: String): SectionTagResolver? = DefaultResolver.tagResolvers.find { it.names.contains(name) }


    /** 标签读取器 **/
    val reader = VariableReader("{", "}")

    /** 标签参数分隔符 **/
    const val TAG_ARG_SEPARATION = ":"

    /**
     * 解析字符串
     */
    fun resolveString(element: String, context: ArgumentContext): String {
        // 读取标签, 拼接字符串片段并返回
        return reader.readToFlatten(element).joinToString("") {
            // 如果标签片段
            val text = if (it.isVariable) {
                // 解析标签
                val handled = resolveTag(it.text, context)
                // 处理无误时返回处理结果, 反则返回完整标签片段
                handled ?: (reader.start + it.text + reader.end)
            } else it.text // 原文本
            resolveOther(text, context) // 解析其他内容
        }
    }

    /**
     * 解析其他的
     */
    @Deprecated("Use ItemResolver API")
    fun resolveOther(text: String, context: ArgumentContext): String {
        val player = context.player()
        if (player != null) {
            // PlaceholderAPI 变量
            return text.replacePlaceholder(player)
        }
        return text
    }

    /**
     * 从标签字符串中获取 [SectionTagResolver] 并解析
     * @return 若标签不合法或者处理后结果为空, 则返回空
     */
    fun resolveTag(tag: String, context: ArgumentContext): String? {
        // 分割
        val split = tag.splitNonEscaped(TAG_ARG_SEPARATION)
        // 获取名称
        val name = split.firstOrNull() ?: return null
        // 获取解析器
        val resolver = match(name) ?: return null
        // 解析并返回
        return try {
            resolver.resolve(split.drop(1), context)
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
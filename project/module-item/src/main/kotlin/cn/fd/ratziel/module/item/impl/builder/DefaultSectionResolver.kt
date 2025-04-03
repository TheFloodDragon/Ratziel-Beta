package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.core.exception.ArgumentNotFoundException
import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.core.util.splitNonEscaped
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import taboolib.common.platform.function.severe
import taboolib.common.platform.function.warning
import taboolib.common.util.VariableReader
import java.util.concurrent.CopyOnWriteArrayList

/**
 * DefaultSectionResolver
 *
 * @author TheFloodDragon
 * @since 2024/8/13 14:42
 */
object DefaultSectionResolver {

    /**
     * 标签解析器列表
     */
    val resolvers: MutableCollection<SectionTagResolver> = CopyOnWriteArrayList()

    /**
     * 只解析字符串形式的 [JsonPrimitive]
     */
    fun resolve(element: JsonElement, context: ArgumentContext): JsonElement {
        return if (element is JsonPrimitive && element.isString) {
            // 解析完后返回
            JsonPrimitive(resolve(element.content, context))
        } else element
    }

    /**
     * 解析字符串
     */
    fun resolve(element: String, context: ArgumentContext): String {
        // 读取标签, 拼接字符串片段并返回
        return reader.readToFlatten(element).joinToString("") {
            // 如果标签片段
            if (it.isVariable) {
                // 处理标签
                val handled = handleTag(it.text, context)
                // 处理无误时返回处理结果, 反则返回完整标签片段
                handled ?: (reader.start + it.text + reader.end)
            } else it.text // 原文本
        }
    }

    /**
     * 从标签字符串中获取 [SectionTagResolver] 并处理
     * @return 若标签不合法或者处理后结果为空, 则返回空
     */
    fun handleTag(tagStr: String, context: ArgumentContext): String? {
        // 分割
        val split = tagStr.splitNonEscaped(TAG_ARG_SEPARATION)
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

    /**
     * 匹配 [SectionTagResolver]
     * @return 匹配不到时返回空
     */
    fun match(name: String): SectionTagResolver? = resolvers.find { it.names.contains(name) }

    /**
     * 标签读取器
     */
    val reader = VariableReader("{", "}")

    /**
     * 标签参数分隔符
     */
    const val TAG_ARG_SEPARATION = ":"

}
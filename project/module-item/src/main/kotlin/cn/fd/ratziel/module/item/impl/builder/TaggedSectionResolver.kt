package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.core.exception.ArgumentNotFoundException
import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.core.serialization.json.JsonTree
import cn.fd.ratziel.core.util.splitNonEscaped
import cn.fd.ratziel.module.item.api.builder.ItemSectionResolver
import cn.fd.ratziel.module.item.api.builder.ItemTagResolver
import taboolib.common.platform.function.severe
import taboolib.common.platform.function.warning
import taboolib.common.util.VariableReader
import java.util.*

/**
 * TaggedSectionResolver
 *
 * 解析带有标签的字符串
 *
 * @author TheFloodDragon
 * @since 2025/5/3 19:34
 */
class TaggedSectionResolver(
    /**
     * 标签解析器列表
     */
    val tagResolvers: Iterable<ItemTagResolver> = emptyList(),
) : ItemSectionResolver {

    constructor(vararg resolvers: ItemTagResolver) : this(listOf(*resolvers))

    /**
     * 解析字符串
     * @return 解析后的字符串
     */
    override fun resolve(section: String, context: ArgumentContext): String {
        // 读取标签, 拼接字符串片段并返回
        val parts = reader.readToFlatten(section)
        return if (parts.isNotEmpty()) {
            parts.joinToString("") {
                // 如果标签片段
                if (it.isVariable) {
                    // 解析标签
                    val handled = resolveTag(it.text, context)
                    // 处理无误时返回处理结果, 反则返回完整标签片段
                    handled ?: (reader.start + it.text + reader.end)
                } else it.text // 原文本
            }
        } else section
    }

    /**
     * 从标签字符串中获取 [ItemTagResolver] 并解析
     * @return 若标签不合法或者处理后结果为空, 则返回空
     */
    private fun resolveTag(tag: String, context: ArgumentContext): String? {
        // 分割
        val split = tag.splitNonEscaped(TAG_ARG_SEPARATION)
        // 获取名称
        val name = split.firstOrNull() ?: return null
        // 获取解析器
        val resolver = tagResolvers.find { it.alias.contains(name) } ?: return null
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


    companion object {

        /** 标签读取器 **/
        @JvmStatic
        private val reader = VariableReader("{", "}")

        /** 标签参数分隔符 **/
        const val TAG_ARG_SEPARATION = ":"

        /**
         * 解析带有单个标签的 [JsonTree]
         * @param resolver 标签解析器
         * @param tree [JsonTree]
         * @param context 上下文
         */
        @JvmStatic
        suspend fun resolveWithSingle(resolver: ItemTagResolver, tree: JsonTree, context: ArgumentContext) {
            val resolver = TaggedSectionResolver(Collections.singletonList(resolver))
            DefaultResolver.resolveTreeWithSectionResolver(resolver, tree, context)
        }

    }

}
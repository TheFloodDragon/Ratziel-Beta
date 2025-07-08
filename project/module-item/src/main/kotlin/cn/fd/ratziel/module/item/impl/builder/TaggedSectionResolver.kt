package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.core.exception.ArgumentNotFoundException
import cn.fd.ratziel.core.functional.ArgumentContext
import cn.fd.ratziel.core.serialization.json.JsonTree
import cn.fd.ratziel.core.util.splitNonEscaped
import cn.fd.ratziel.module.item.api.builder.ItemSectionResolver
import cn.fd.ratziel.module.item.api.builder.ItemTagResolver
import taboolib.common.platform.function.severe
import taboolib.common.platform.function.warning
import taboolib.common.util.VariableReader
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

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
    val tagResolvers: Iterable<ItemTagResolver>,
) : ItemSectionResolver {

    /**
     * 解析字符串
     * @return 解析后的字符串
     */
    override fun resolve(node: JsonTree.Node, context: ArgumentContext) {
        val section = node.stringSection() ?: return
        // 读取标签, 拼接字符串片段并返回
        val parts = reader.readToFlatten(section.value.content)
        if (parts.isNotEmpty()) {
            val resolved = parts.joinToString("") {
                // 如果标签片段
                if (it.isVariable) {
                    // 解析标签
                    val handled = resolveTag(it.text, context)
                    // 处理无误时返回处理结果, 反则返回完整标签片段
                    handled ?: (reader.start + it.text + reader.end)
                } else it.text // 原文本
            }
            section.value(resolved)
        }
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
        try {
            // 解析节点 (包括所有类型的节点)
            return resolver.resolve(split.drop(1), context)
        } catch (ex: ArgumentNotFoundException) {
            warning("Missing argument '${ex.missingType.simpleName}' for $resolver")
        } catch (ex: Exception) {
            severe("Failed to resolve element by $resolver!")
            ex.printStackTrace()
        }
        return null
    }

    companion object {

        /**
         * 默认的标签解析器列表
         */
        val defaultTagResolvers: MutableList<ItemTagResolver> = CopyOnWriteArrayList()

        /**
         * 注册默认的标签解析器
         */
        @JvmStatic
        fun registerTagResolver(resolver: ItemTagResolver) {
            defaultTagResolvers.add(resolver)
        }

        /** 标签读取器 **/
        @JvmStatic
        private val reader = VariableReader("{", "}")

        /** 标签参数分隔符 **/
        const val TAG_ARG_SEPARATION = ":"

        @JvmStatic
        fun single(resolver: ItemTagResolver) = TaggedSectionResolver(Collections.singletonList(resolver))

        /**
         * 解析带有单个标签的 [JsonTree]
         * @param resolver 标签解析器
         * @param tree [JsonTree]
         * @param context 上下文
         */
        @JvmStatic
        fun resolveWithSingle(resolver: ItemTagResolver, tree: JsonTree, context: ArgumentContext) {
            DefaultResolver.resolveTree(tree, context, Collections.singletonList(single(resolver)))
        }

    }

}
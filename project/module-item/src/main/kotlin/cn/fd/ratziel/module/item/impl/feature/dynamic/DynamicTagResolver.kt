package cn.fd.ratziel.module.item.impl.feature.dynamic

import cn.fd.ratziel.common.element.registry.AutoRegister
import cn.fd.ratziel.core.functional.ArgumentContext
import cn.fd.ratziel.core.util.splitNonEscaped
import cn.fd.ratziel.module.item.api.builder.ItemTagResolver
import cn.fd.ratziel.module.item.impl.builder.TaggedSectionResolver
import java.util.regex.Pattern

/**
 * DynamicTagResolver
 *
 * @author TheFloodDragon
 * @since 2025/5/31 16:49
 */
@AutoRegister
object DynamicTagResolver : ItemTagResolver {

    override val alias = arrayOf("dynamic", "d")

    const val IDENTIFIED_START = "{\${"
    const val IDENTIFIED_END = "}$}"
    const val IDENTIFIED_SEPARATION = TaggedSectionResolver.TAG_ARG_SEPARATION

    val regex: Pattern = Pattern.compile("\\{\\$\\{([\\s\\S]*?)}\\$}")

    override fun resolve(args: List<String>, context: ArgumentContext): String? {
        // 解析器名称
        val name = args.firstOrNull() ?: return null
        // 获取动态解析器
        DynamicTagService.findResolver(name) ?: return null

        // 动态解析标识内容
        return IDENTIFIED_START + args.joinToString(IDENTIFIED_SEPARATION) + IDENTIFIED_END
    }

    fun resolveTag(content: String, context: ArgumentContext): String? {
        // 获取文本 (去除头尾)
        val text = content.drop(IDENTIFIED_START.length).dropLast(IDENTIFIED_END.length)
        // 分割内容
        val split = text.splitNonEscaped(IDENTIFIED_SEPARATION)

        // 获取解析器
        val name = split.firstOrNull() ?: return null
        val resolver = DynamicTagService.findResolver(name) ?: return null
        // 解析并返回结果
        return resolver.resolve(split.drop(1), context)
    }

}
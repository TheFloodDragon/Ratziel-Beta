package cn.fd.ratziel.module.item.feature.dynamic

import cn.fd.ratziel.common.element.registry.AutoRegister
import cn.fd.ratziel.core.contextual.ArgumentContext
import cn.fd.ratziel.core.contextual.AttachedContext
import cn.fd.ratziel.core.util.splitNonEscaped
import cn.fd.ratziel.module.item.api.builder.ItemTagResolver
import cn.fd.ratziel.module.item.impl.builder.provided.TaggedSectionResolver
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

    @JvmStatic
    internal val isDynamic by AttachedContext.catcher { false }

    const val IDENTIFIED_START = $$"{${"
    const val IDENTIFIED_END = "}$}"
    const val IDENTIFIED_SEPARATION = TaggedSectionResolver.TAG_ARG_SEPARATION

    @JvmField
    val regex: Pattern = Pattern.compile("\\{\\$\\{([\\s\\S]*?)}\\$}")

    override fun prepare(args: List<String>, context: ArgumentContext) {
        // 解析器名称
        val name = args.first()
        // 获取动态解析器
        val resolver = DynamicTagService.findResolver(name)
        // 预解析
        resolver.prepare(args.drop(1), context)
        // 标记此物品存在动态标签 (是动态物品, 用于提升效率)
        isDynamic[context] = true
    }

    override fun resolve(args: List<String>, context: ArgumentContext): String {
        // 动态解析标识内容
        return args.joinToString(
            prefix = IDENTIFIED_START,
            separator = IDENTIFIED_SEPARATION,
            postfix = IDENTIFIED_END
        )
    }

    @JvmStatic
    fun resolveTag(content: String, context: ArgumentContext): String? {
        // 获取文本 (去除头尾)
        val text = content.substring(IDENTIFIED_START.length, content.length - IDENTIFIED_END.length)
        // 分割内容
        val split = text.splitNonEscaped(IDENTIFIED_SEPARATION)

        // 获取解析器
        val name = split.first()
        val resolver = DynamicTagService.findResolver(name)
        // 解析并返回结果
        return resolver.resolve(split.drop(1), context)
    }

}

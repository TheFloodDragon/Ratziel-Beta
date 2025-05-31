package cn.fd.ratziel.module.item.impl.feature.dynamic

import cn.fd.ratziel.common.element.registry.AutoRegister
import cn.fd.ratziel.core.function.ArgumentContext
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

    override fun resolve(assignment: ItemTagResolver.Assignment, context: ArgumentContext) {
        // 解析器名称
        val name = assignment.args.firstOrNull() ?: return
        // 获取动态解析器
        DynamicTagService.findResolver(name) ?: return

        // 动态解析识别内容
        val identifiedContent = IDENTIFIED_START + assignment.args.joinToString(IDENTIFIED_SEPARATION) + IDENTIFIED_END

        // 原任务返回识别内容
        assignment.complete(identifiedContent)
    }

}
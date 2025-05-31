package cn.fd.ratziel.module.item.impl.feature.dynamic

import cn.fd.ratziel.common.element.registry.AutoRegister
import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.module.item.api.builder.ItemTagResolver
import cn.fd.ratziel.module.item.impl.builder.TaggedSectionResolver
import pers.neige.neigeitems.utils.StringUtils.joinToString

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

    override fun resolve(assignment: ItemTagResolver.Assignment, context: ArgumentContext) {
        // 解析器名称
        val name = assignment.args.firstOrNull() ?: return
        // 获取动态解析器
        val resolver = DynamicTagService.findResolver(name) ?: return

        // 动态解析识别内容
        val droppedArgs = assignment.args.drop(1)
        val identifiedContent = IDENTIFIED_START + droppedArgs.joinToString(IDENTIFIED_SEPARATION) + IDENTIFIED_END
        // 创建解析任务并提交到解析服务
        val resolvation = DynamicTagResolvation(identifiedContent, resolver, droppedArgs)
        DynamicTagService.submit(resolvation)

        // 原任务返回识别内容
        assignment.complete(identifiedContent)
    }

}
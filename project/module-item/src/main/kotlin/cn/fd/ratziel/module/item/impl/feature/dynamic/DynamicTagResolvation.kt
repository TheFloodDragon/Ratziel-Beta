package cn.fd.ratziel.module.item.impl.feature.dynamic

import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.module.item.api.builder.ItemTagResolver
import kotlin.jvm.optionals.getOrNull

/**
 * DynamicTagResolvation
 *
 * @author TheFloodDragon
 * @since 2025/5/31 17:14
 */
class DynamicTagResolvation(
    /**
     * 用于识别动态字符串的内容
     */
    val identifiedContent: String,
    /**
     * 动态标签解析器
     */
    val resolver: ItemTagResolver,
    /**
     * 标签参数
     */
    val args: List<String>,
) {

    /**
     * 解析标签
     */
    fun resolve(context: ArgumentContext): String? {
        // 创建新的解析任务
        val assignment = ItemTagResolver.Assignment(args, null)
        // 调用解析器进行解析
        resolver.resolve(assignment, context)
        // 返回解析结果
        return assignment.result.getOrNull()
    }

}
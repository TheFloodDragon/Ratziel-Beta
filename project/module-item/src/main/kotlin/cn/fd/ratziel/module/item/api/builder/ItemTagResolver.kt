package cn.fd.ratziel.module.item.api.builder

import cn.fd.ratziel.core.contextual.ArgumentContext

/**
 * ItemTagResolver
 *
 * @author TheFloodDragon
 * @since 2025/5/24 17:36
 */
interface ItemTagResolver {

    val alias: Array<String>

    /**
     * 准备解析阶段处理
     */
    fun prepare(args: List<String>, context: ArgumentContext) = Unit

    /**
     * 解析元素
     *
     * @param args 参数
     * @param context 上下文
     * @return 解析结果 (空代表解析未完成, 不替换内容)
     */
    fun resolve(args: List<String>, context: ArgumentContext): String?

}
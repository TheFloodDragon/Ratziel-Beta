package cn.fd.ratziel.module.item.impl.builder.resolver

import cn.fd.ratziel.core.util.splitNonEscaped
import cn.fd.ratziel.function.argument.ArgumentContext
import cn.fd.ratziel.module.item.impl.builder.resolver.sectionResolvers.PapiResolver
import cn.fd.ratziel.module.item.impl.builder.resolver.sectionResolvers.RandomResolver
import taboolib.common.util.VariableReader

/**
 * BasicTagResolver - 解析 [SectionResolver.TagResolver]
 *
 * @author TheFloodDragon
 * @since 2024/7/8 17:51
 */
object BasicTagResolver : SectionResolver.StringResolver {

    /**
     * 标签解析器
     */
    val resolvers: MutableList<SectionResolver.TagResolver> = mutableListOf(
        RandomResolver, PapiResolver.TagResolver
    )

    /**
     * 参数分割符
     */
    const val ARGUMENT_SEPRATION_SIGN = ":"

    /**
     * 变量读取器
     */
    val reader = VariableReader("{", "}")

    override fun resolve(element: String, context: ArgumentContext): String =
        reader.readToFlatten(element).joinToString("") {
            // 如果是标签, 则通过标签解析器解析
            if (it.isVariable) {
                handle(it.text, context)
            } else it.text // 不然就是它本身
        } // 拼接结果成字符串并返回

    /**
     * 寻找 [SectionResolver.TagResolver] 并处理
     */
    fun handle(source: String, context: ArgumentContext): String {
        // 分割
        val split = source.splitNonEscaped(ARGUMENT_SEPRATION_SIGN)
        // 获取名称
        val name = split.firstOrNull() ?: return source
        // 获取解析器
        val resolver = resolvers.find { it.names.contains(name) }
        // 解析并返回
        return resolver?.resolve(split.drop(1), context) ?: (reader.start + source + reader.end)
    }

}
package cn.fd.ratziel.module.item.impl.builder.resolver

import cn.fd.ratziel.module.item.api.ArgumentResolver

/**
 * SectionTagResolver
 *
 * @author TheFloodDragon
 * @since 2024/6/25 20:04
 */
interface SectionTagResolver : ArgumentResolver<Iterable<String>, String?> {

    /**
     * 解析器名称
     */
    val name: String

    /**
     * 解析器别名
     */
    val alias: Array<String>

}
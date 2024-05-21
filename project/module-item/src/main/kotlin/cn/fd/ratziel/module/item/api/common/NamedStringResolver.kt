package cn.fd.ratziel.module.item.api.common

/**
 * NamedStringResolver
 *
 * @author TheFloodDragon
 * @since 2024/5/21 22:55
 */
interface NamedStringResolver : StringResolver {

    /**
     * 解析器名称
     */
    val name: String

    /**
     * 解析器别名
     */
    val alias: Array<String>

}
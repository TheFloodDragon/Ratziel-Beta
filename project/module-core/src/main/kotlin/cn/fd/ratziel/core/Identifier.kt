package cn.fd.ratziel.core

/**
 * Identifier
 *
 * @author TheFloodDragon
 * @since 2024/6/24 13:46
 */
interface Identifier {

    /**
     * 获取标识符的字符串内容
     */
    val content: String

    /**
     * 此标识符的 [hashCode]
     */
    override fun hashCode(): Int

}
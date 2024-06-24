package cn.fd.ratziel

/**
 * Identifier
 *
 * @author TheFloodDragon
 * @since 2024/6/24 13:46
 */
interface Identifier {


    /**
     * 判断此标识符是否与另一个对象相同
     */
    override fun equals(other: Any?): Boolean

    /**
     * 获取标识符的字符串形式
     */
    override fun toString(): String

    /**
     * 此标识符的Hash码
     */
    override fun hashCode(): Int

}
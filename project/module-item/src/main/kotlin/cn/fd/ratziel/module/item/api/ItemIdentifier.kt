package cn.fd.ratziel.module.item.api

/**
 * ItemIdentifier - 物品标识符
 *
 * @author TheFloodDragon
 * @since 2024/5/3 14:46
 */
interface ItemIdentifier {

    /**
     * 将标识符转换为[String]形式
     */
    override fun toString(): String

    /**
     * 判断此物品标识符是否与另一个物品标识符相同
     */
    override fun equals(other: Any?): Boolean

    /**
     * 获取 [hashCode] - 用于 [toString]
     */
    override fun hashCode(): Int

}
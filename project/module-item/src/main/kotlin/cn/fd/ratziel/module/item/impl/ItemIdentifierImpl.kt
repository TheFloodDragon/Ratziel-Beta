package cn.fd.ratziel.module.item.impl

import cn.fd.ratziel.module.item.api.ItemIdentifier
import java.util.*

/**
 * ItemIdentifierImpl
 *
 * @author TheFloodDragon
 * @since 2024/5/3 14:47
 */
data class ItemIdentifierImpl(
    val uuid: UUID,
) : ItemIdentifier {

    override fun asString() = uuid.toString()

    override fun isIdentical(other: ItemIdentifier) = this.asString() == other.asString()

    companion object {

        /**
         * 随机生成一个物品标识符
         */
        @JvmStatic
        fun random(): ItemIdentifier = ItemIdentifierImpl(UUID.randomUUID())

        /**
         * 由字符串获取物品标识符
         */
        @JvmStatic
        fun fromString(name: String): ItemIdentifier = ItemIdentifierImpl(UUID.fromString(name))

    }

}
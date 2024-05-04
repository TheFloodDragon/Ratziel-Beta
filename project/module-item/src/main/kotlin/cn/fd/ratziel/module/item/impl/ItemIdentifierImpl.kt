package cn.fd.ratziel.module.item.impl

import cn.fd.ratziel.module.item.api.ItemIdentifier
import java.util.*

/**
 * ItemIdentifierImpl
 *
 * @author TheFloodDragon
 * @since 2024/5/3 14:47
 */
open class ItemIdentifierImpl(
    val id: String,
) : ItemIdentifier {

    constructor(uuid: UUID) : this(uuid.toString())

    override fun equals(other: Any?) = id == other

    override fun toString() = id

    override fun hashCode() = id.hashCode()

    companion object {

        /**
         * 随机生成一个物品标识符
         */
        @JvmStatic
        fun random(): ItemIdentifier = ItemIdentifierImpl(UUID.randomUUID())

    }

}
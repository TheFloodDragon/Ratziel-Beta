package cn.fd.ratziel.core

import java.util.*

/**
 * IdentifierImpl
 *
 * @author TheFloodDragon
 * @since 2024/6/24 13:47
 */
open class IdentifierImpl(val unique: String) : Identifier {

    constructor(uuid: UUID) : this(uuid.toString())

    constructor() : this(UUID.randomUUID())

    override fun equals(other: Any?): Boolean = unique == other

    override fun toString(): String = unique

    override fun hashCode(): Int = unique.hashCode()

}
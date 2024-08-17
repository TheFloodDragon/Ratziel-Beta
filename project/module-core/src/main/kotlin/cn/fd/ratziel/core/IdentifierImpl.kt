package cn.fd.ratziel.core

import java.util.*

/**
 * IdentifierImpl
 *
 * @author TheFloodDragon
 * @since 2024/6/24 13:47
 */
@JvmInline
value class IdentifierImpl(override val content: String) : Identifier {

    constructor(uuid: UUID) : this(uuid.toString())

    constructor() : this(UUID.randomUUID())

    override fun toString() = "Identifier(unique=$content)"

}
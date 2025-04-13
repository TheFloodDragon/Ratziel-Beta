package cn.fd.ratziel.core

import cn.fd.ratziel.core.util.randomUuid

/**
 * SimpleIdentifier
 *
 * @author TheFloodDragon
 * @since 2024/6/24 13:47
 */
@JvmInline
value class SimpleIdentifier(override val content: String) : Identifier {

    constructor() : this(randomUuid())

    override fun toString() = "SimpleIdentifier($content)"

}
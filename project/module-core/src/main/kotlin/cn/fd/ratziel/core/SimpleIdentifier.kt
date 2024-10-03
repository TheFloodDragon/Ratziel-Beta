@file:OptIn(ExperimentalUuidApi::class)

package cn.fd.ratziel.core

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toKotlinUuid

/**
 * SimpleIdentifier
 *
 * @author TheFloodDragon
 * @since 2024/6/24 13:47
 */
@JvmInline
value class SimpleIdentifier(override val content: String) : Identifier {

    constructor() : this(Uuid.random())

    constructor(uuid: Uuid) : this(uuid.toHexString())

    constructor(uuid: java.util.UUID) : this(uuid.toKotlinUuid())

    override fun toString() = "SimpleIdentifier($content)"

}
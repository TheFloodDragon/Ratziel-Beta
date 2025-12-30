package cn.fd.ratziel.module.item.impl.component

import cn.altawk.nbt.NbtPath
import cn.fd.ratziel.core.Identifier

/**
 * NbtNodeIdentifier
 * 
 * @author TheFloodDragon
 * @since 2025/12/30 22:56
 */
data class NbtNodeIdentifier(val path: NbtPath) : Identifier {

    constructor(name: String) : this(NbtPath(name))

    override val content get() = path.toString()

}
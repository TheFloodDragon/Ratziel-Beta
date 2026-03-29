package cn.fd.ratziel.module.item.impl.component.transformers

import cn.altawk.nbt.NbtPath
import cn.altawk.nbt.tag.NbtByte
import cn.altawk.nbt.tag.NbtCompound
import cn.fd.ratziel.module.item.api.component.transformer.NbtTransformer
import cn.fd.ratziel.module.nbt.delete
import cn.fd.ratziel.module.nbt.read
import cn.fd.ratziel.module.nbt.write
import taboolib.module.nms.MinecraftVersion.versionId

/**
 * UnbreakableNbtTransformer
 *
 * @author TheFloodDragon
 * @since 2026/3/22 03:04
 */
object UnbreakableNbtTransformer : NbtTransformer<Boolean> {

    private val path = NbtPath(
        NbtPath.NameNode(if (versionId >= 12005) "minecraft:unbreakable" else "Unbreakable")
    )

    override fun write(root: NbtCompound, component: Boolean) {
        if (versionId >= 12005) {
            if (component) root.write(path, NbtCompound(), true) else root.delete(path)
        } else {
            root.write(path, NbtByte(component), true)
        }
    }

    override fun read(root: NbtCompound): Boolean? {
        val tag = root.read(path, false) ?: return null
        return if (versionId >= 12005) {
            when (tag) {
                is NbtByte -> tag.content.toInt() != 0
                else -> true
            }
        } else {
            (tag as? NbtByte)?.content?.toInt()?.let { it != 0 }
        }
    }

    override fun remove(root: NbtCompound) {
        root.delete(path)
    }

}

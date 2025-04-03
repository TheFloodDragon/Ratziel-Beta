package cn.fd.ratziel.module.nbt

import cn.altawk.nbt.NbtPath
import cn.altawk.nbt.tag.NbtCompound
import cn.altawk.nbt.tag.NbtList
import cn.altawk.nbt.tag.NbtTag

/**
 * NbtHelper
 *
 * @author TheFloodDragon
 * @since 2024/8/12 19:41
 */
object NbtHelper {

    /**
     * 通过路径获取 [NbtCompound] 并进行处理
     * @param path 路径 (最后一个节点必须为 [NbtPath.NameNode])
     */
    @JvmStatic
    fun handle(tag: NbtCompound, path: Iterable<NbtPath.Node>, create: Boolean = true, action: NbtCompound.() -> Unit) {
        val find = read(tag, path, create) as? NbtCompound ?: return
        action(find)
    }

    /**
     * 通过路径读取 [NbtTag]
     */
    @JvmStatic
    fun read(tag: NbtCompound, path: Iterable<NbtPath.Node>, create: Boolean = false): NbtTag? {
        var result: NbtTag = tag
        for (node in path) {
            when (node) {
                is NbtPath.NameNode ->
                    if (result is NbtCompound) {
                        result = result[node.name]
                            ?: if (create) NbtCompound().also { result[node.name] = it } else return null
                    }

                is NbtPath.IndexNode ->
                    if (result is NbtList) {
                        result = result.getOrNull(node.index)
                            ?: if (create) NbtList().also { result[node.index] = it } else return null
                    }
            }
        }
        return result
    }

}

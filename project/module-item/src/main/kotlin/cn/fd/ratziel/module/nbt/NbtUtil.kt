@file:Suppress("NOTHING_TO_INLINE")

package cn.fd.ratziel.module.nbt

import cn.altawk.nbt.NbtPath
import cn.altawk.nbt.tag.NbtByte
import cn.altawk.nbt.tag.NbtCompound
import cn.altawk.nbt.tag.NbtTag

/**
 * NbtUtil
 *
 * @author TheFloodDragon
 * @since 2025/3/29 20:19
 */


/**
 * 读取 [NbtTag]
 */
inline fun NbtCompound.read(path: NbtPath, create: Boolean = true): NbtTag? = NbtHelper.read(this, path, create)


/**
 * 通过路径获取 [NbtCompound] 并进行处理
 */
inline fun NbtCompound.handle(path: NbtPath, create: Boolean = true, noinline action: NbtCompound.() -> Unit) = NbtHelper.handle(this, path, create, action)

/**
 * 读取 [String]
 */
inline fun NbtCompound.readString(node: String): String? = this[node]?.content as? String

/**
 * 读取 [Int]
 */
inline fun NbtCompound.readInt(node: String): Int? = this[node]?.content as? Int

/**
 * 读取 [Byte]
 */
inline fun NbtCompound.readByte(node: String): Byte? = this[node]?.content as? Byte

/**
 * 读取 [Boolean]
 */
inline fun NbtCompound.readBoolean(node: String): Boolean? = readByte(node)?.let { NbtByte(it).toBoolean() }
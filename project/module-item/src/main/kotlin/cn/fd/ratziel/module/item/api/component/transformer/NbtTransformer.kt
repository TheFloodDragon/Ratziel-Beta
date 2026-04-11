package cn.fd.ratziel.module.item.api.component.transformer

import cn.altawk.nbt.tag.NbtCompound

/**
 * NbtTransformer - 面向根 [NbtCompound] 的组件转换器
 *
 * 负责将组件数据写入、读取以及从根 NBT 中移除。
 *
 * @author TheFloodDragon
 * @since 2026/1/1 21:27
 */
interface NbtTransformer<T> {

    /**
     * 从根 [NbtCompound] 中读取组件数据。
     *
     * @return 根数据不合法或不存在该组件数据时，可返回 null
     */
    fun readFrom(root: NbtCompound): T?

    /**
     * 将组件数据写入到根 [NbtCompound]。
     */
    fun writeTo(root: NbtCompound, component: T)

    /**
     * 从根 [NbtCompound] 中移除该组件对应的数据。
     */
    fun removeFrom(root: NbtCompound)

}

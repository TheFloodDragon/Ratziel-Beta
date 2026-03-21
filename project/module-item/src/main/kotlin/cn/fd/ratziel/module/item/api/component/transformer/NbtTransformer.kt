package cn.fd.ratziel.module.item.api.component.transformer

import cn.altawk.nbt.tag.NbtCompound
import cn.altawk.nbt.tag.NbtTag


/**
 * NbtTransformer - [NbtTag] 数据类型转换
 *
 * @author TheFloodDragon
 * @since 2026/1/1 21:27
 */
interface NbtTransformer<T> {

    /**
     * 组件 -> [NbtTag]
     */
    fun toNbtTag(component: T, root: NbtCompound): NbtTag

    /**
     * [NbtTag] -> 组件
     *
     * @return 传入数据不合法或者没有被转换信息时, 可返回 null
     */
    fun fromNbtTag(tag: NbtTag): T?

}
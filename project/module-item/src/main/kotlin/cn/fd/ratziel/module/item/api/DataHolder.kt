package cn.fd.ratziel.module.item.api

import cn.altawk.nbt.tag.NbtTag

/**
 * DataHolder
 *
 * @author TheFloodDragon
 * @since 2025/4/3 22:55
 */
interface DataHolder {

    /**
     * 获取 [NbtTag] 数据
     *
     * @param name 数据名称
     */
    operator fun get(name: String): NbtTag?

    /**
     * 设置 [NbtTag] 数据
     *
     * @param name 数据名称
     * @param tag 具体数据
     */
    operator fun set(name: String, tag: NbtTag)

}
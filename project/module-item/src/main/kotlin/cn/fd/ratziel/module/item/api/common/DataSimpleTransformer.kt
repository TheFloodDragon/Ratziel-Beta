package cn.fd.ratziel.module.item.api.common

import cn.fd.ratziel.module.item.api.DataTransformer
import cn.fd.ratziel.module.item.nbt.NBTCompound

/**
 * DataSimpleTransformer
 *
 * @author TheFloodDragon
 * @since 2024/3/23 13:13
 */
interface DataSimpleTransformer<T> : DataTransformer<T, NBTCompound> {

    /**
     * 正向转化 - 输出型转化
     * @param source 源标签
     */
    fun transform(target: T, source: NBTCompound): NBTCompound

    /**
     * 正向转化 - 输出型转化
     * 重写并调用 [transform] 方法, 默认使用空的 [NBTCompound] 作为源数据
     */
    override fun transform(input: T): NBTCompound = transform(input, NBTCompound())

}
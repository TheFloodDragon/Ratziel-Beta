package cn.fd.ratziel.module.item.api.common

import cn.fd.ratziel.module.item.api.Transformer
import cn.fd.ratziel.module.item.nbt.NBTCompound
import cn.fd.ratziel.module.item.nbt.NBTData

/**
 * DataTransformer - 物品数据转换器
 *
 * @author TheFloodDragon
 * @since 2024/3/16 10:48
 */
interface DataTransformer<T, D : NBTData> : Transformer<T, D>

/**
 * DataCTransformer
 * 使用 [NBTCompound] 作为输出类型
 *
 * @author TheFloodDragon
 * @since 2024/3/16 10:50
 */
interface DataCTransformer<T> : DataTransformer<T, NBTCompound> {

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
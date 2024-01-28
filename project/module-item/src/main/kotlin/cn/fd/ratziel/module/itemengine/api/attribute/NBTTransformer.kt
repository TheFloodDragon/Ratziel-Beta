package cn.fd.ratziel.module.itemengine.api.attribute

import cn.fd.ratziel.module.itemengine.nbt.NBTTag

/**
 * NBTTransformer - NBT属性转换器
 *
 * @author TheFloodDragon
 * @since 2023/12/8 21:42
 */
interface NBTTransformer<T> : AttributeTransformer<T, NBTTag> {

    /**
     * 正向转化 - 输出型转化
     * @param source 源标签
     */
    fun transform(target: T, source: NBTTag): NBTTag

    /**
     * 正向转化 - 输出型转化
     * 重写并调用 [transform] 方法, 默认使用空的NBT标签作为源标签
     */
    override fun transform(target: T): NBTTag = transform(target, NBTTag())

}
package cn.fd.ratziel.module.itemengine.api.attribute

import cn.fd.ratziel.module.itemengine.nbt.NBTTag

/**
 * NBTTransformer - NBT属性转换器
 *
 * @author TheFloodDragon
 * @since 2023/12/8 21:42
 */
interface NBTTransformer<T> : AttributeTransformer<T, NBTTag> {

    fun transform(target: T, from: NBTTag): NBTTag

    override fun transform(target: T): NBTTag = transform(target, NBTTag())

}
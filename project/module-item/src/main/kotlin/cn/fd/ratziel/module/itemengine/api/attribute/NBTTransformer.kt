package cn.fd.ratziel.module.itemengine.api.attribute

import cn.fd.ratziel.module.itemengine.api.attribute.core.AttributeTransformer
import cn.fd.ratziel.module.itemengine.nbt.NBTTag

/**
 * NBTTransformer - NBT属性转换器
 *
 * @author TheFloodDragon
 * @since 2023/12/8 21:42
 */
interface NBTTransformer : AttributeTransformer<NBTTag> {

    fun transform(source: NBTTag): NBTTag

    override fun transform(): NBTTag = transform(NBTTag())

}
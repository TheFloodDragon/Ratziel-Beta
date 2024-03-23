package cn.fd.ratziel.module.item.nbt

/**
 * [NBTCompound.DeepVisitor] 系列方法
 */
fun NBTCompound.getDeep(node: String) = NBTCompound.DeepVisitor.getDeep(this, node)
fun NBTCompound.putDeep(node: String, value: NBTData) = NBTCompound.DeepVisitor.putDeep(this, node, value)
fun NBTCompound.removeDeep(node: String) = NBTCompound.DeepVisitor.removeDeep(this, node)
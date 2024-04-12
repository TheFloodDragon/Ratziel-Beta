package cn.fd.ratziel.module.item.nbt

/**
 * [NBTCompound.DeepVisitor] 系列方法
 */
fun NBTCompound.getDeep(node: String) = NBTCompound.DeepVisitor.getDeep(this, node)
fun NBTCompound.putDeep(node: String, value: NBTData, checkList: Boolean = true) = NBTCompound.DeepVisitor.putDeep(this, node, value, checkList)
fun NBTCompound.removeDeep(node: String) = NBTCompound.DeepVisitor.removeDeep(this, node)

fun NBTList.setSafely(index: Int, value: NBTData) = if (index == sourceList.size) add(value) else set(index, value)

fun NBTList.setSafely(index: Int, value: NBTData, typeCheck: Boolean) =
    // 开启类型检查, 列表不为空情况下的类型不匹配
    if (typeCheck && !isEmpty() && get(0)!!.type != value.type) error("It's not allowed to set a ${value.type} data in this list.") else setSafely(index, value)
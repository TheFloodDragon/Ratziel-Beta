//package cn.fd.ratziel.module.itemengine.api.part
//
//import cn.fd.ratziel.core.util.digest
//import cn.fd.ratziel.module.itemengine.nbt.NBTTag
//
///**
// * ItemData - 物品数据 ([NBTTag]的封装)
// *
// * @author TheFloodDragon
// * @since 2024/1/26 20:46
// */
//open class ItemData(rawData: Any) : NBTTag(rawData) {
//
//    override fun clone() = ItemData(super.clone())
//
//    /**
//     * 哈希值 (SHA-256)
//     */
//    val hash get() = data.toString().digest()
//
//}
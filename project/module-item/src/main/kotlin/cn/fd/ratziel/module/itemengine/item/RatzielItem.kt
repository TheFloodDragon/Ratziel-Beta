//package cn.fd.ratziel.module.itemengine.item
//
//import cn.fd.ratziel.module.itemengine.api.NeoItem
//import cn.fd.ratziel.module.itemengine.api.attribute.ItemAttribute
//import cn.fd.ratziel.module.itemengine.api.part.ItemData
//import cn.fd.ratziel.module.itemengine.api.part.ItemMaterial
//import cn.fd.ratziel.module.itemengine.util.applyFrom
//import cn.fd.ratziel.module.itemengine.util.applyTo
//import java.util.function.Consumer
//
///**
// * RatzielItem - Ratziel 物品
// *
// * @author TheFloodDragon
// * @since 2023/10/27 22:10
// */
//data class RatzielItem(
//    /**
//     * 物品材料
//     */
//    override val material: ItemMaterial,
//    /**
//     * 物品数据
//     */
//    override val data: ItemData,
//) : NeoItem {
//
//    /**
//     * 通过物品属性的转化器转化数据,并对其进行操作
//     * @param attribute 要转化成的物品属性基底, 一般是一个新的物品属性对象
//     * @param block 对转化后物品属性的操作
//     */
//    fun <T : ItemAttribute<T>> with(attribute: T, block: Consumer<T>) = this.apply {
//        // 将物品数据转化成物品属性
//        val handle = attribute.apply { attribute.applyFrom(data) }
//        // 对转化后的物品属性进行操作
//        block.accept(handle)
//        // 应用操作后的物品属性至物品属性上
//        handle.applyTo(data)
//    }
//
//}
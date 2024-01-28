package cn.fd.ratziel.module.itemengine.api

import cn.fd.ratziel.module.itemengine.api.attribute.ItemAttribute
import cn.fd.ratziel.module.itemengine.api.part.ItemData
import cn.fd.ratziel.module.itemengine.api.part.ItemInfo
import cn.fd.ratziel.module.itemengine.api.part.ItemMaterial
import cn.fd.ratziel.module.itemengine.api.part.ItemPart
import java.util.function.Consumer

/**
 * NeoItem - 物品
 *
 * @author TheFloodDragon
 * @since 2023/10/27 22:21
 */
interface NeoItem : ItemPart {

    /**
     * 物品材质
     */
    val material: ItemMaterial

    /**
     * 物品数据
     */
    val data: ItemData

    /**
     * 物品信息
     */
    val info: ItemInfo? get() = data[ItemInfo.NODE_ITEM]?.let { ItemInfo(it) }

    /**
     * 通过物品属性的转化器转化数据,并对其进行操作
     */
    fun <T : ItemAttribute<T>> with(attribute: T, block: Consumer<T>) =
        block.accept(attribute.apply { transformer.detransform(attribute.value, data) })

}
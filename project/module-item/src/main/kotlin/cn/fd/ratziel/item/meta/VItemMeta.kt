@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.item.meta

import cn.fd.ratziel.item.api.ItemMetadata
import cn.fd.ratziel.item.api.TranslatableItemTag
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import taboolib.module.nms.ItemTagData

/**
 * VItemMeta
 *
 * @author TheFloodDragon
 * @since 2023/10/14 16:15
 */
@Serializable(ItemMetaSerializer::class)
open class VItemMeta(
    override var display: VItemDisplay,
    @JsonNames("char", "chars", "feature", "features")
    override var characteristic: VItemCharacteristic,
    override var durability: VItemDurability,
) : ItemMetadata , TranslatableItemTag {

    //    /**
//     * 应用 Bukkit.ItemMeta
//     * @param replace 如果元数据存在,是否替换 (默认true)
//     */
//    fun applyForm(
//        meta: ItemMeta, replace: Boolean = true,
//        /**
//         * 值的设置
//         * 使用变量的形式是为了自定义性
//         */
//        fDisplayName: Consumer<VItemMeta> = Consumer {
//            if (it.display.name == null || replace)
//                it.display.setName(meta.getProperty("displayName"))
//        },
//        fLocalizedName: Consumer<VItemMeta> = Consumer {
//            if (it.characteristic.localizedName == null || replace)
//                it.characteristic.localizedName = meta.localizedName
//        },
//        fLore: Consumer<VItemMeta> = Consumer {
//            if (it.display.lore.isEmpty() || replace)
//                it.display.setLore(meta.getProperty("lore") ?: emptyList())
//        },
//        fCustomModelData: Consumer<VItemMeta> = Consumer {
//            if (it.characteristic.customModelData == null || replace)
//                it.characteristic.customModelData = getProperty<Int>("customModelData")
//        },
//        fEnchants: Consumer<VItemMeta> = Consumer {
//            if (it.characteristic.enchants.isEmpty() || replace)
//                it.characteristic.enchants = meta.enchants
//        },
//        fItemFlags: Consumer<VItemMeta> = Consumer {
//            if (it.characteristic.itemFlags.isEmpty() || replace)
//                it.characteristic.itemFlags = meta.itemFlags
//        },
//        fUnbreakable: Consumer<VItemMeta> = Consumer {
//            it.characteristic.unbreakable = meta.isUnbreakable
//        },
//        fAttributeModifiers: Consumer<VItemMeta> = Consumer {
//            if (it.characteristic.attributeModifiers.isEmpty() || replace)
//                it.characteristic.attributeModifiers.apply {
//                    meta.attributeModifiers?.forEach { key, value ->
//                        characteristic.addAttributeModifiers(key, value)
//                    }
//                }
//        },
//    ) {
//        fDisplayName.accept(this)
//        fLocalizedName.accept(this)
//        fLore.accept(this)
//        if (MinecraftVersion.isHigherOrEqual(MinecraftVersion.V1_14))
//            fCustomModelData.accept(this)
//        fEnchants.accept(this)
//        fItemFlags.accept(this)
//        fUnbreakable.accept(this)
//        fAttributeModifiers.accept(this)
//    }
    override fun toItemTag(): ItemTagData {
        TODO("Not yet implemented")
    }

}
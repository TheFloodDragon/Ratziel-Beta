package cn.fd.ratziel.item.meta

import cn.fd.ratziel.item.api.ItemMetadata
import kotlinx.serialization.Serializable
import org.bukkit.inventory.meta.ItemMeta
import taboolib.library.reflex.Reflex.Companion.getProperty
import taboolib.module.nms.MinecraftVersion
import java.util.function.Consumer

/**
 * VItemMeta
 *
 * @author TheFloodDragon
 * @since 2023/10/14 16:15
 */
@Serializable
class VItemMeta : ItemMetadata {

    override val display: VItemDisplay? = null

    override val characteristic: VItemCharacteristic? = null

    /**
     * 通过Bukkit.ItemMeta构造
     */
    constructor(meta: ItemMeta, replace: Boolean = true) {
        applyForm(meta, replace)
    }

    /**
     * 应用 Bukkit.ItemMeta
     * @param replace 如果元数据存在,是否替换 (默认true)
     */
    fun applyForm(
        meta: ItemMeta, replace: Boolean = true,
        /**
         * 值的设置
         * 使用变量的形式是为了自定义性
         */
        fDisplayName: Consumer<VItemMeta> = Consumer {
            if (it.display?.name == null || replace)
                it.display?.name = meta.getProperty("displayName")
        },
        fLocalizedName: Consumer<VItemMeta> = Consumer {
            if (it.characteristic?.localizedName == null || replace)
                it.characteristic?.localizedName = meta.localizedName
        },
        fLore: Consumer<VItemMeta> = Consumer {
            if (it.display?.lore?.isEmpty() == true || replace)
                it.display?.lore = meta.getProperty("lore") ?: emptyList()
        },
        fCustomModelData: Consumer<VItemMeta> = Consumer {
            if (it.characteristic?.customModelData == null || replace)
                it.characteristic?.customModelData = getProperty<Int>("customModelData")
        },
        fEnchants: Consumer<VItemMeta> = Consumer {
            if (it.characteristic?.enchants?.isEmpty() == true || replace)
                it.characteristic?.enchants = meta.enchants
        },
        fItemFlags: Consumer<VItemMeta> = Consumer {
            if (it.characteristic?.itemFlags?.isEmpty() == true || replace)
                it.characteristic?.itemFlags = meta.itemFlags
        },
        fUnbreakable: Consumer<VItemMeta> = Consumer {
            it.characteristic?.unbreakable = meta.isUnbreakable
        },
        fAttributeModifiers: Consumer<VItemMeta> = Consumer {
            if (it.characteristic?.attributeModifiers?.isEmpty() == true || replace)
                it.characteristic?.attributeModifiers.apply {
                    meta.attributeModifiers?.forEach { key, value ->
                        characteristic?.addAttributeModifiers(key, value)
                    }
                }
        },
    ) {
        fDisplayName.accept(this)
        fLocalizedName.accept(this)
        fLore.accept(this)
        if (MinecraftVersion.isHigherOrEqual(MinecraftVersion.V1_14))
            fCustomModelData.accept(this)
        fEnchants.accept(this)
        fItemFlags.accept(this)
        fUnbreakable.accept(this)
        fAttributeModifiers.accept(this)
    }

}
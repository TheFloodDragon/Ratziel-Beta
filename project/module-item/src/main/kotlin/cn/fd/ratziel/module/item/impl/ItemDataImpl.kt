package cn.fd.ratziel.module.item.impl

import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.ItemMaterial
import cn.fd.ratziel.module.item.nbt.NBTCompound
import cn.fd.ratziel.module.item.nbt.ProxyNBTCompound

/**
 * ItemDataImpl
 *
 * @author TheFloodDragon
 * @since 2024/5/5 13:33
 */
data class ItemDataImpl(
    /**
     * 物品材料
     */
    override var material: ItemMaterial = ItemMaterial.EMPTY,
    /**
     * 物品NBT
     */
    override var nbt: NBTCompound = ProxyNBTCompound(),
    /**
     * 物品数量
     */
    override var amount: Int = 1
) : ItemData
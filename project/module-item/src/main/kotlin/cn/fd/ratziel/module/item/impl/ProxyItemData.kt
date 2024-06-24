package cn.fd.ratziel.module.item.impl

import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.ItemMaterial
import cn.fd.ratziel.module.item.nbt.NBTCompound

/**
 * ProxyItemData
 *
 * @author TheFloodDragon
 * @since 2024/6/24 14:37
 */
class ProxyItemData(val parent: ItemData, theTag: NBTCompound) : ItemData {

    override var material: ItemMaterial
        get() = parent.material
        set(value) {
            parent.material = value
        }

    override val tag: NBTCompound = theTag

    override var amount: Int
        get() = parent.amount
        set(value) {
            parent.amount = value
        }

}
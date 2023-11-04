package cn.fd.ratziel.module.item.api

import cn.fd.ratziel.bukkit.util.nbt.NBTInt
import cn.fd.ratziel.bukkit.util.nbt.NBTString
import cn.fd.ratziel.bukkit.util.nbt.NBTTag
import taboolib.common.platform.function.pluginId

/**
 * ItemInfo - 物品信息
 *
 * @author TheFloodDragon
 * @since 2023/10/28 21:39
 */
data class ItemInfo(
    /**
     * 物品唯一标识符
     */
    val id: String,
    /**
     * 储存的数据
     */
    var data: NBTTag?,
) : ItemPart {

    constructor(nbtTag: NBTTag) : this(
        nbtTag.getDeep("$pluginId.id")!!.asString(), nbtTag.getDeep("$pluginId.data") as NBTTag
    )

    /**
     * 哈希值
     */
    val hash: Int
        get() = data.hashCode()

    /**
     * 将物品信息转化成NBT标签的形式
     */
    fun toNBTTag() = NBTTag().also { apex ->
        apex[pluginId] = NBTTag().apply {
            this["id"] = NBTString(id)
            this["data"] = data ?: NBTTag()
            this["hash"] = NBTInt(hash)
        }
    }

}
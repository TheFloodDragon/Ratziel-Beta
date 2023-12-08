package cn.fd.ratziel.module.itemengine.api.part

import cn.fd.ratziel.module.itemengine.nbt.NBTInt
import cn.fd.ratziel.module.itemengine.nbt.NBTString
import cn.fd.ratziel.module.itemengine.nbt.TiNBTTag
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
    var data: TiNBTTag?,
) : ItemPart {

    constructor(nbtTag: TiNBTTag) : this(
        nbtTag.getDeep("$pluginId.id")!!.asString(), nbtTag.getDeep("$pluginId.data") as TiNBTTag
    )

    /**
     * 哈希值
     */
    val hash: Int
        get() = data.hashCode()

    /**
     * 将物品信息转化成NBT标签的形式
     */
    fun toNBTTag() = TiNBTTag().also { apex ->
        apex[pluginId] = TiNBTTag().apply {
            this["id"] = NBTString(id).getAsTiNBT()
            this["data"] = data ?: TiNBTTag()
            this["hash"] = NBTInt(hash).getAsTiNBT()
        }
    }

}
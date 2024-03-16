package cn.fd.ratziel.module.item.api.part

import cn.fd.ratziel.common.message.MessageComponent
import cn.fd.ratziel.core.serialization.Opt
import cn.fd.ratziel.module.item.api.ItemComponent
import cn.fd.ratziel.module.item.nbt.NBTCompound

/**
 * ItemDisplay
 *
 * @author TheFloodDragon
 * @since 2024/3/16 10:56
 */
interface ItemDisplay : ItemComponent<ItemDisplay, NBTCompound> {

    /**
     * 物品名称
     */
    val name: Opt<MessageComponent>

    /**
     * 物品本地化名称
     */
    val localizedName: Opt<MessageComponent>

    /**
     * 物品描述
     */
    val lore: Opt<List<MessageComponent>>

    /**
     * 设置名称
     */
    fun setName(name: String)

    /**
     * 设置描述
     */
    fun setLore(lore: Iterable<String>)

    /**
     * 设置物品本地化名称
     */
    fun setLocalizedName(localizedName: String)

}
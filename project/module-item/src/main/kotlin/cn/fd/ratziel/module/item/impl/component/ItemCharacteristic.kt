@file:OptIn(ExperimentalSerializationApi::class)
@file:Suppress("DEPRECATION")

package cn.fd.ratziel.module.item.impl.component

import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.ItemNode
import cn.fd.ratziel.module.item.api.ItemTransformer
import cn.fd.ratziel.module.item.impl.ItemDataImpl
import cn.fd.ratziel.module.item.impl.component.util.HeadUtil
import cn.fd.ratziel.module.item.nbt.NBTCompound
import cn.fd.ratziel.module.item.nbt.NBTInt
import cn.fd.ratziel.module.item.nbt.read
import cn.fd.ratziel.module.item.nbt.write
import cn.fd.ratziel.module.item.nms.ItemSheet
import cn.fd.ratziel.module.item.nms.RefItemMeta
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import org.bukkit.inventory.meta.SkullMeta
import taboolib.library.xseries.XSkull
import taboolib.platform.util.getSkullValue

/**
 * ItemCharacteristic
 *
 * @author TheFloodDragon
 * @since 2024/6/29 15:20
 */
@Serializable
data class ItemCharacteristic(
    /**
     * 头颅元数据:
     * 使用字符串存储, 值的类型详见 [XSkull.ValueType]
     */
    @JsonNames("skull", "skull-meta", "skullMeta", "head", "head-meta")
    var headMeta: String? = null,
    /**
     * 染色皮革物品的颜色
     */
    @JsonNames("color", "leather-color")
    var leatherColor: String? = null,
) {

    companion object : ItemTransformer<ItemCharacteristic> {

        override val node = ItemNode.ROOT

        override fun transform(component: ItemCharacteristic): ItemData {
            // 头颅处理
            val tag = component.headMeta?.let { HeadUtil.getHeadTag(it) } ?: NBTCompound()
            tag.write(ItemSheet.DYED_COLOR, component.leatherColor?.let { Integer.valueOf(it) }?.let { NBTInt(it) })
            return ItemDataImpl(tag = tag)
        }

        override fun detransform(data: ItemData): ItemCharacteristic {
            val impl = ItemCharacteristic()
            // 头颅处理
            val skullMeta = RefItemMeta(RefItemMeta.skullClass, data.tag).handle as? SkullMeta
            impl.headMeta = skullMeta?.owner ?: skullMeta?.getSkullValue() ?: return impl
            // 皮革颜色处理
            data.tag.read<NBTInt>(ItemSheet.DYED_COLOR) {
                impl.leatherColor = it.content.toString()
            }
            // 返回
            return impl
        }

    }

}
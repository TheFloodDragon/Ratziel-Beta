package cn.fd.ratziel.module.item.impl

import cn.fd.ratziel.core.Identifier
import cn.fd.ratziel.core.IdentifierImpl
import cn.fd.ratziel.module.item.api.NeoItem
import cn.fd.ratziel.module.item.nbt.NBTLong
import cn.fd.ratziel.module.item.nbt.NBTString
import cn.fd.ratziel.module.item.nms.RefItemStack
import cn.fd.ratziel.module.item.util.ComponentUtil
import org.bukkit.inventory.ItemStack

/**
 * ItemInfo
 *
 * @author TheFloodDragon
 * @since 2024/6/28 15:27
 */
data class ItemInfo(
    /**
     * 物品唯一标识符 [Identifier]
     */
    val identifier: Identifier,
    /**
     * 物品构建(完成)的时间
     */
    val built_date: Long,
) {


    companion object {

        val INFO_NODE = OccupyNode("info", OccupyNode.RATZIEL_NODE)

        val NODE_ID = "identifier"

        val NODE_BUILT_DATE = "built_date"

        /**
         * 向 [NeoItem] 中写入 [ItemInfo]
         */
        fun write(info: ItemInfo, item: NeoItem) {
            // 寻找标签: custom_data.Ratziel.info
            val tag = ComponentUtil.findByNode(item.data.tag, INFO_NODE)
            // 写入数据
            tag[NODE_ID] = NBTString(info.identifier.toString())
            tag[NODE_BUILT_DATE] = NBTLong(info.built_date)
        }

        /**
         * 从 [NeoItem] 中读取 [ItemInfo]
         */
        fun read(item: NeoItem): ItemInfo? {
            // 寻找标签: custom_data.Ratziel.info
            val tag = ComponentUtil.findByNodeOrNull(item.data.tag, INFO_NODE) ?: return null
            // 读取数据
            val id = tag[NODE_ID] as? NBTString ?: return null
            val date = tag[NODE_BUILT_DATE] as? NBTLong ?: return null
            // 合成结果
            return ItemInfo(
                identifier = IdentifierImpl(id.content),
                built_date = date.content,
            )
        }

        /**
         * 判断
         */
        fun isRatziel(item: ItemStack): Boolean {
            val ref = RefItemStack(item)
            val custom = ref.getCustomTag() ?: return false
            return custom.containsKey(OccupyNode.RATZIEL_NODE.name)
        }

    }

}
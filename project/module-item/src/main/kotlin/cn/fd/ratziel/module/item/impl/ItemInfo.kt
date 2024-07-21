package cn.fd.ratziel.module.item.impl

import cn.fd.ratziel.core.Identifier
import cn.fd.ratziel.core.IdentifierImpl
import cn.fd.ratziel.module.item.api.NeoItem
import cn.fd.ratziel.module.item.nbt.NBTCompound
import cn.fd.ratziel.module.item.nbt.NBTInt
import cn.fd.ratziel.module.item.nbt.NBTString
import cn.fd.ratziel.module.item.util.ComponentUtil

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
    val id: Identifier,
    /**
     * 物品类型 (物品元素名称)
     */
    val type: String,
    /**
     * 物品元素内容的哈希值
     */
    val hash: Int,
) {

    companion object {

        /**
         * [RatzielItem] 物品数据节点
         */
        val RATZIEL_NODE = OccupyNode("Ratziel", OccupyNode.CUSTOM_DATA_NODE)

        /**
         * [RatzielItem] 物品标识符
         */
        val RATZIEL_IDENTIFIER_NODE = OccupyNode("Identifier", RATZIEL_NODE)

        /**
         * [RatzielItem] 物品元素名称
         */
        val RATZIEL_ELEMENT_NODE = OccupyNode("Type", RATZIEL_NODE)

        /**
         * [RatzielItem] 物品元素内容哈希值
         */
        val RATZIEL_HASH_NODE = OccupyNode("Hash", RATZIEL_NODE)

        /**
         * 向 [NBTCompound] 中写入 [ItemInfo]
         */
        fun write(info: ItemInfo, tag: NBTCompound) {
            // 寻找数据: custom_data.Ratziel.info
            val data = ComponentUtil.findByNode(tag, RATZIEL_NODE)
            // 写入数据
            data[RATZIEL_IDENTIFIER_NODE.name] = NBTString(info.id.toString())
            data[RATZIEL_ELEMENT_NODE.name] = NBTString(info.type)
            data[RATZIEL_HASH_NODE.name] = NBTInt(info.hash)
        }

        /**
         * 从 [NBTCompound] 中读取 [ItemInfo]
         */
        fun read(tag: NBTCompound): ItemInfo? {
            // 寻找数据: custom_data.Ratziel.info
            val data = ComponentUtil.findByNodeOrNull(tag, RATZIEL_NODE) ?: return null
            // 读取数据
            val id = data[RATZIEL_IDENTIFIER_NODE.name] as? NBTString ?: return null
            val name = data[RATZIEL_ELEMENT_NODE.name] as? NBTString ?: return null
            val hash = data[RATZIEL_HASH_NODE.name] as? NBTInt ?: return null
            // 合成结果
            return ItemInfo(
                id = IdentifierImpl(id.content),
                type = name.content,
                hash = hash.content,
            )
        }

        /**
         * 向 [NeoItem] 中写入 [ItemInfo]
         */
        fun write(info: ItemInfo, item: NeoItem) = write(info, item.data.tag)

    }

}
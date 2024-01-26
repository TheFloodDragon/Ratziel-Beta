package cn.fd.ratziel.module.itemengine.api.part

import cn.fd.ratziel.module.itemengine.api.attribute.ItemAttribute
import cn.fd.ratziel.module.itemengine.nbt.NBTString
import cn.fd.ratziel.module.itemengine.nbt.NBTTag

/**
 * ItemInfo - 物品信息
 *
 * @author TheFloodDragon
 * @since 2023/10/28 21:39
 */
open class ItemInfo(
    /**
     * 物品数据
     */
    val data: ItemData,
) : ItemAttribute<ItemInfo> {

    init {
        try {
            id // 获取标识符以校验数据
        } catch (_: Exception) {
            throw error("Unsupported Data \"$data\"!")
        }
    }

    /**
     * 物品标识符
     */
    val id: String get() = (getInfo()[NODE_IDENTIFIER] as NBTString).content

    /**
     * 自定义数据
     */
    val customData get() = (data[NODE_CUSTOM_DATA] as? NBTTag)?.let { ItemData(it) }

    /**
     * 获取当前数据存储的哈希值
     */
    val hashStored: String? get() = (getInfo()[NODE_HASH] as? NBTString)?.content

    /**
     * 获取当前数据的哈希值
     */
    val hashNow: String get() = dropInfo(data.clone()).hash

    /**
     * 获取物品信息
     */
    internal fun getInfo() = data[NODE_INFO] as NBTTag

    override fun transform(source: NBTTag) = source.merge(data)

    override fun detransform(input: NBTTag) {
        data.merge(input)
    }

    companion object {

        /**
         * 所有特殊数据存储的顶级节点
         */
        const val NODE_ITEM = "NeoItem"

        /**
         * 信息节点 [NODE_ITEM].[NODE_INFO]
         */
        const val NODE_INFO = "info"

        /**
         * 标识符节点 [NODE_ITEM].[NODE_INFO].[NODE_IDENTIFIER]
         */
        const val NODE_IDENTIFIER = "id"

        /**
         * 哈希节点 [NODE_ITEM].[NODE_INFO].[NODE_HASH]
         */
        const val NODE_HASH = "hash"

        /**
         * 自定义数据节点 [NODE_ITEM].[NODE_CUSTOM_DATA]
         */
        const val NODE_CUSTOM_DATA = "data"

        /**
         * 丢弃物品信息数据 (不克隆)
         */
        fun dropInfo(target: ItemData) = target.apply { remove(NODE_INFO) }

    }

    override fun getNode() = NODE_ITEM

}
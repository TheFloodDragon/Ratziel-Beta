package cn.fd.ratziel.module.item.impl

import cn.fd.ratziel.core.Identifier
import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.NeoItem
import cn.fd.ratziel.module.item.impl.service.GlobalServiceManager
import cn.fd.ratziel.module.item.nms.RefItemStack
import org.bukkit.inventory.ItemStack

/**
 * RatzielItem
 *
 * @author TheFloodDragon
 * @since 2024/5/2 22:05
 */
open class RatzielItem : NeoItem {

    constructor(info: ItemInfo) : this(info, ItemDataImpl())

    constructor(info: ItemInfo, data: ItemData) {
        // 写入数据
        ItemInfo.write(info, data.tag)
        // 设置值
        this.info = info
        this.data = data
    }

    /**
     * 物品信息
     */
    val info: ItemInfo

    /**
     * 物品唯一标识符
     */
    val identifier: Identifier get() = info.id

    /**
     * 物品数据
     */
    final override var data: ItemData
        protected set

    /**
     * 物品服务
     */
    override val service get() = GlobalServiceManager[identifier]

    companion object {

        /**
         * 将目标 [ItemStack] 转为 [RatzielItem]
         */
        fun of(item: ItemStack): RatzielItem? {
            val ref = RefItemStack(item)
            // 获取物品数据
            val data = ref.getData() ?: return null
            // 获取物品信息
            val info = ItemInfo.read(data.tag) ?: return null
            return RatzielItem(info, data)
        }

        /**
         * 判断目标 [ItemStack] 是否为 [RatzielItem]
         */
        fun isRatzielItem(item: ItemStack): Boolean {
            val ref = RefItemStack(item)
            val custom = ref.getCustomTag() ?: return false
            return custom.containsKey(ItemInfo.RATZIEL_NODE.name)
        }

    }

}
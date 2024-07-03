package cn.fd.ratziel.module.item.impl

import cn.fd.ratziel.core.Identifier
import cn.fd.ratziel.core.IdentifierImpl
import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.NeoItem
import cn.fd.ratziel.module.item.impl.service.GlobalServiceManager
import cn.fd.ratziel.module.item.nbt.NBTString
import cn.fd.ratziel.module.item.nms.RefItemStack
import cn.fd.ratziel.module.item.util.ComponentUtil
import org.bukkit.inventory.ItemStack

/**
 * RatzielItem
 *
 * @author TheFloodDragon
 * @since 2024/5/2 22:05
 */
open class RatzielItem : NeoItem {

    constructor(data: ItemData) : this(IdentifierImpl(), data)

    constructor() : this(ItemDataImpl())

    constructor(identifier: Identifier, data: ItemData) {
        this.identifier = identifier
        this.data = data
    }

    /**
     * 物品唯一标识符
     */
    val identifier: Identifier

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
            val identifier = ComponentUtil.findByNode(data.tag, OccupyNode.RATZIEL_NODE)[OccupyNode.RATZIEL_IDENTIFIER_NODE.name] as? NBTString ?: return null
            return RatzielItem(IdentifierImpl(identifier.content), data)
        }

        /**
         * 判断目标 [ItemStack] 是否为 [RatzielItem]
         */
        fun isRatzielItem(item: ItemStack): Boolean {
            val ref = RefItemStack(item)
            val custom = ref.getCustomTag() ?: return false
            return custom.containsKey(OccupyNode.RATZIEL_NODE.name)
        }

    }

}
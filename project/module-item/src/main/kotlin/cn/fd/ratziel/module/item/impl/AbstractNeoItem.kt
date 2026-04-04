package cn.fd.ratziel.module.item.impl

import cn.fd.ratziel.module.item.api.IdentifiedItem
import cn.fd.ratziel.module.item.api.component.ItemComponentData
import cn.fd.ratziel.module.item.api.component.ItemComponentHolder

/**
 * AbstractNeoItem
 *
 * @author TheFloodDragon
 * @since 2025/5/3 17:57
 */
abstract class AbstractNeoItem(
    override val data: ItemComponentData,
) : IdentifiedItem, ItemComponentHolder by data {

    /**
     * 消耗一定数量的物品
     *
     * @return 是否成功消耗
     */
    fun take(amount: Int): Boolean {
        val current = this.data.amount
        val took = current - amount
        if (took < 0) {
            return false
        } else {
            this.data.amount = took
            return true
        }
    }

}

package cn.fd.ratziel.module.item.impl

import cn.fd.ratziel.module.item.api.IdentifiedItem
import cn.fd.ratziel.module.item.api.component.ItemComponentHolder
import cn.fd.ratziel.module.item.api.component.ItemComponentType
import cn.fd.ratziel.module.item.internal.RefItemStack

/**
 * AbstractNeoItem
 *
 * TODO 修改结构以优化性能
 *
 * @author TheFloodDragon
 * @since 2025/5/3 17:57
 */
abstract class AbstractNeoItem : IdentifiedItem, ItemComponentHolder {

    override fun <T : Any> get(type: ItemComponentType<T>): T? {
        val nmsItem = RefItemStack.of(this.data).nmsStack ?: return null
        return type.transforming.minecraftTransformer.read(nmsItem)
    }

    override fun <T : Any> set(type: ItemComponentType<T>, value: T) {
        editNmsItem { type.transforming.minecraftTransformer.write(it, value) }
    }

    override fun remove(type: ItemComponentType<*>) {
        editNmsItem { type.transforming.minecraftTransformer.remove(it) }
    }

    private fun editNmsItem(action: (Any) -> Unit) {
        val ref = RefItemStack.of(this.data)
        val nmsItem = ref.nmsStack ?: return
        action(nmsItem)
        val updated = ref.extractData()
        this.data.material = updated.material
        this.data.tag = updated.tag
        this.data.amount = updated.amount
    }

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

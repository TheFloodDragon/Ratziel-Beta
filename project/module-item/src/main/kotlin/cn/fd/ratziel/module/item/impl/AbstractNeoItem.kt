package cn.fd.ratziel.module.item.impl

import cn.altawk.nbt.tag.NbtCompound
import cn.fd.ratziel.module.item.ItemRegistry
import cn.fd.ratziel.module.item.api.ComponentHolder
import cn.fd.ratziel.module.item.api.IdentifiedItem
import cn.fd.ratziel.module.item.util.ComponentConverter

/**
 * AbstractNeoItem
 *
 * @author TheFloodDragon
 * @since 2025/5/3 17:57
 */
abstract class AbstractNeoItem : IdentifiedItem, ComponentHolder {

    /**
     * 获取组件
     *
     * @param type 组件类型
     */
    override fun <T> getComponent(type: Class<T>): T {
        val integrated = ItemRegistry.getComponent(type)
        return ComponentConverter.deserializeFromNbtTag(integrated, this.data.tag)
    }

    /**
     * 设置组件
     *
     * @param component 组件
     */
    override fun setComponent(component: Any) {
        @Suppress("UNCHECKED_CAST")
        val integrated = ItemRegistry.getComponent(component::class.java) as ItemRegistry.ComponentIntegrated<Any>
        // 将组件序列化成标签
        val tag = ComponentConverter.serializeToNbtTag(integrated, component)
        if (tag is NbtCompound) {
            // 合并标签
            this.data.tag.mergeShallow(tag, true)
        }
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
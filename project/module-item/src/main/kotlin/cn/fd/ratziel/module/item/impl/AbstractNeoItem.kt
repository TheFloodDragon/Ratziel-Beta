package cn.fd.ratziel.module.item.impl

import cn.altawk.nbt.tag.NbtCompound
import cn.fd.ratziel.module.item.ItemElement
import cn.fd.ratziel.module.item.ItemRegistry
import cn.fd.ratziel.module.item.api.NeoItem

/**
 * AbstractNeoItem
 *
 * @author TheFloodDragon
 * @since 2025/5/3 17:57
 */
abstract class AbstractNeoItem : NeoItem {

    /**
     * 获取组件
     *
     * @param type 组件类型
     */
    fun <T> getComponent(type: Class<T>): T {
        val integrated = ItemRegistry.getComponent(type)
        // 将标签反序列成组件
        val component = ItemElement.nbt.decodeFromNbtTag(integrated.serializer, this.data.tag)
        // 返回组件
        return component
    }

    /**
     * 设置组件
     *
     * @param component 组件
     */
    fun setComponent(component: Any) {
        val integrated = ItemRegistry.getComponent(component::class.java) as ItemRegistry.Integrated<Any>
        // 将组件序列化成标签
        val tag = ItemElement.nbt.encodeToNbtTag(integrated.serializer, component)
        if (tag is NbtCompound) {
            // 合并标签
            this.data.tag.merge(tag, true)
        }
    }

}
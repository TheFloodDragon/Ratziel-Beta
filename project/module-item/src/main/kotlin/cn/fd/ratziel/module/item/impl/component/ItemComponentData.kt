package cn.fd.ratziel.module.item.impl.component

import cn.altawk.nbt.tag.NbtTag

/**
 * ItemComponentData
 *
 * @author TheFloodDragon
 * @since 2025/11/15 10:36
 */
interface ItemComponentData {

    /**
     * 数据组件类型 [net.minecraft.core.component.DataComponentType]
     */
    val type: NamespacedIdentifier

    /**
     * 数据实例的 [NbtTag] 形式 (原物品数据的副本)
     * (空的时候代表数据被删除)
     */
    val tag: NbtTag?

    /**
     * 是否是被删除的组件
     */
    val removed: Boolean

    companion object {

        /**
         * 创建一个组件数据
         *
         * @param tag 空的时候代表数据被删除
         */
        @JvmStatic
        fun of(type: NamespacedIdentifier, tag: NbtTag?): ItemComponentData {
            return Provided(type, tag)
        }

        /**
         * 创建懒标签获取数据
         */
        @JvmStatic
        fun lazyGetter(type: NamespacedIdentifier, removed: Boolean, getTag: () -> NbtTag?): ItemComponentData {
            return Lazied(type, removed, getTag)
        }

    }

    private class Lazied(
        type: NamespacedIdentifier,
        removed: Boolean,
        getTag: () -> NbtTag?,
    ) : Provided(type, null, removed) {
        override val tag by lazy(getTag)
        override fun toString() = "LaziedComponentData(type=$type, tag=$tag, removed=$removed)"
    }

    private open class Provided(
        override val type: NamespacedIdentifier,
        override val tag: NbtTag?,
        override val removed: Boolean = tag == null,
    ) : ItemComponentData {
        override fun toString() =
            if (removed) "RemovedComponentData(type=$type)"
            else "ProvidedComponentData(type=$type, tag=$tag)"
    }

}
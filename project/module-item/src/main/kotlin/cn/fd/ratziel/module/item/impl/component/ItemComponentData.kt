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
     * 数据实例的 [NbtTag] 形式 (原物品数据的副本)
     * (空的时候代表数据被删除)
     */
    val tag: NbtTag?

    /**
     * 是否是被删除的组件
     */
    val removed: Boolean get() = true

    /**
     * 删除的数据
     */
    object Removed : ItemComponentData {
        override val tag get() = null
        override val removed get() = false
        override fun toString() = "ItemComponentData.Removed"
    }

    companion object {

        /**
         * 创建一个组件数据
         *
         * @param tag 空的时候代表数据被删除
         */
        @JvmStatic
        fun of(tag: NbtTag): ItemComponentData {
            return Provided(tag)
        }

        /**
         * 创建一个清空的组件数据
         */
        @JvmStatic
        fun removed() = Removed

        /**
         * 创建懒标签获取数据
         */
        @JvmStatic
        fun lazyGetter(removed: Boolean, getTag: () -> NbtTag?): ItemComponentData {
            return Lazied(removed, getTag)
        }

    }

    private class Lazied(
        override val removed: Boolean,
        getTag: () -> NbtTag?,
    ) : ItemComponentData {
        override val tag by lazy(getTag)
        override fun toString() = if (removed) "ItemComponentData.Removed" else "ItemComponentData(tag=$tag)"
    }

    private open class Provided(
        override val tag: NbtTag,
    ) : ItemComponentData {
        override fun toString() = "ItemComponentData(tag=$tag)"
    }

}
package cn.fd.ratziel.module.ui.inventory.click

/**
 * InventoryClickAction
 *
 * A tagged union representing possible clicks from the client.
 *
 * @author TheFloodDragon
 * @since 2025/10/5 13:59
 */
sealed interface InventoryClickAction {

    /**
     * 点击的栏位
     */
    val slot: Int

    /**
     * 点击类型
     */
    val type: InventoryClickType

    /**
     * 鼠标点击操作 - 只涉及到一个有效栏位的 (拖动、点击容器外不算)
     * 支持的类型见 [MouseClick.acceptableTypes]
     */
    data class MouseClick(override val slot: Int, override val type: InventoryClickType) : InventoryClickAction {
        init {
            require(type in acceptableTypes)
        }

        companion object {
            @JvmStatic
            private val acceptableTypes = arrayOf(
                InventoryClickType.LEFT_CLICK,
                InventoryClickType.RIGHT_CLICK,
                InventoryClickType.MIDDLE_CLICK,
                InventoryClickType.SHIFT_LEFT_CLICK,
                InventoryClickType.SHIFT_RIGHT_CLICK,
                InventoryClickType.DOUBLE_CLICK,
            )
        }
    }

    /**
     * 丢弃操作, 包括 按下丢弃键 和 左右键点击容器外.
     */
    data class Drop(
        /**
         * 按下丢弃键丢弃时, [slot] 为光标对准的那个栏位.
         * 当左右键容器外时, [slot] 为 -999.
         */
        override val slot: Int,
        override val type: InventoryClickType,
    ) : InventoryClickAction {
        init {
            require(type in acceptableTypes)
        }

        companion object {
            @JvmStatic
            private val acceptableTypes = arrayOf(
                InventoryClickType.DROP,
                InventoryClickType.CONTROL_DROP,
                InventoryClickType.LEFT_CLICK_OUTSIDE,
                InventoryClickType.RIGHT_CLICK_OUTSIDE,
                InventoryClickType.MIDDLE_CLICK_OUTSIDE,
            )
        }
    }

    /**
     * 切换副手
     */
    data class OffhandSwap(override val slot: Int) : InventoryClickAction {
        override val type get() = InventoryClickType.OFFHAND_SWAP
    }

    /**
     * 数字快捷键切换
     */
    data class HotbarSwap(val hotbarSlot: Int, override val slot: Int) : InventoryClickAction {
        override val type = InventoryClickType.numberKey(hotbarSlot)
    }

    /**
     * 拖动完成后触发
     */
    data class Drag(
        /**
         * Returns the list of slots. When the event inventory is the opened inventory, slots greater than its size
         * indicate slots in the player inventory; subtract the size of the event inventory to get the player inventory
         * slot.
         */
        val slots: Set<Int>,
        override val type: InventoryClickType,
    ) : InventoryClickAction {
        init {
            require(type.isDrag)
        }

        /**
         * 拖动是否结束
         */
        val isEnd =
            type == InventoryClickType.LEFT_MOUSE_DRAG_END
                    || type == InventoryClickType.RIGHT_MOUSE_DRAG_END
                    || type == InventoryClickType.MIDDLE_MOUSE_DRAG_END

        /**
         * 上一个拖动时的栏位
         */
        override val slot: Int = slots.lastOrNull() ?: -1
    }

}

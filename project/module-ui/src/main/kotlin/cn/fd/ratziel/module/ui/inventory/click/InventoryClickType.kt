package cn.fd.ratziel.module.ui.inventory.click

/**
 * InventoryClickType
 *
 * @author TheFloodDragon
 * @since 2025/10/5 13:01
 */
enum class InventoryClickType(val button: Int) {

    /** 鼠标左键 **/
    LEFT_CLICK(0),

    /** 鼠标右键 **/
    RIGHT_CLICK(1),

    /** Shift + 鼠标左键 **/
    SHIFT_LEFT_CLICK(0),

    /** Shift + 鼠标右键 **/
    SHIFT_RIGHT_CLICK(1),

    /** 双击 (鼠标左键) **/
    DOUBLE_CLICK(0),

    /** 鼠标左键点击容器外 **/
    LEFT_CLICK_OUTSIDE(0),

    /** 鼠标右键点击容器外 **/
    RIGHT_CLICK_OUTSIDE(1),

    /** 切换副手 **/
    OFFHAND_SWAP(40),

    /** 丢弃键 **/
    DROP(0),

    /** Ctrl + 丢弃键 **/
    CONTROL_DROP(1),

    /** 数字快捷键切换物品 **/
    NUMBER_KEY_1(0),
    NUMBER_KEY_2(1),
    NUMBER_KEY_3(2),
    NUMBER_KEY_4(3),
    NUMBER_KEY_5(4),
    NUMBER_KEY_6(5),
    NUMBER_KEY_7(6),
    NUMBER_KEY_8(7),
    NUMBER_KEY_9(8),

    /** 鼠标中键 (仅创建模式) **/
    MIDDLE_CLICK(2),

    /** 鼠标中键容器外 (仅创建模式) **/
    MIDDLE_CLICK_OUTSIDE(2),

    /** 左键拖动 **/
    LEFT_MOUSE_DRAG_START(0),
    LEFT_MOUSE_DRAG_ADD_SLOT(1),
    LEFT_MOUSE_DRAG_END(2),

    /** 右键拖动 **/
    RIGHT_MOUSE_DRAG_START(4),
    RIGHT_MOUSE_DRAG_ADD_SLOT(5),
    RIGHT_MOUSE_DRAG_END(6),

    /** 中键拖动 (仅创建模式) **/
    MIDDLE_MOUSE_DRAG_START(8),
    MIDDLE_MOUSE_DRAG_ADD_SLOT(9),
    MIDDLE_MOUSE_DRAG_END(10);

    /** 是否左键点击 **/
    val isLeftClick by lazy { this == LEFT_CLICK || this == SHIFT_LEFT_CLICK || this == DOUBLE_CLICK }

    /** 是否右键点击 **/
    val isRightClick by lazy { this == RIGHT_CLICK || this == SHIFT_RIGHT_CLICK }

    /** 是否为 Shift + 鼠标点击 **/
    val isShiftClick by lazy { this == SHIFT_LEFT_CLICK || this == SHIFT_RIGHT_CLICK }

    /** 是否为鼠标点击 **/
    val isMouseClick by lazy { this.isLeftClick || this.isRightClick || this == MIDDLE_CLICK }

    /** 是否为丢弃操作 **/
    val isDrop by lazy { this == DROP || this == CONTROL_DROP }

    /** 是否为外部点击 **/
    val isOutsideAction by lazy { this == LEFT_CLICK_OUTSIDE || this == RIGHT_CLICK_OUTSIDE || this == MIDDLE_CLICK_OUTSIDE }

    /** 是否为数字快捷键点击 **/
    val isNumberKey by lazy { this.name.startsWith("NUMBER_KEY") }

    /** 是否为键盘点击 **/
    val isKeyboardClick by lazy { this == DROP || this == CONTROL_DROP || this == OFFHAND_SWAP || isNumberKey }

    /** 是否为拖动操作 **/
    val isDrag by lazy { this.name.contains("DRAG") }

    /** 是否为拖动结束 **/
    val isDragEnd by lazy { isDrag && this.name.contains("END") }

    /** 是否为鼠标中键操作 (创造模式下的操作) **/
    val isCreativeAction by lazy { this == MIDDLE_CLICK || this == MIDDLE_CLICK_OUTSIDE || this == MIDDLE_MOUSE_DRAG_START || this == MIDDLE_MOUSE_DRAG_ADD_SLOT || this == MIDDLE_MOUSE_DRAG_END }

    /** 是否会直接移动物品 (而不是简单的拿去放下) **/
    val isItemMove by lazy { isShiftClick || isKeyboardClick || this == DOUBLE_CLICK || isCreativeAction }

    companion object {

        @JvmStatic
        fun numberKey(hotbar: Int): InventoryClickType {
            return InventoryClickType.entries
                .find { it.isNumberKey && it.button == hotbar }
                ?: throw IllegalStateException("Invalid hotbar for number key: $hotbar")
        }

    }

}

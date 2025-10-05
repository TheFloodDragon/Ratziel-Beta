package cn.fd.ratziel.module.ui.inventory.click

import cn.fd.ratziel.module.ui.inventory.click.InventoryClickAction.*
import cn.fd.ratziel.module.ui.inventory.click.InventoryClickPreprocessor.Mode.*
import cn.fd.ratziel.module.ui.inventory.click.InventoryClickType.*
import cn.fd.ratziel.platform.bukkit.util.readOrThrow
import taboolib.module.nms.Packet
import java.util.function.Supplier


/**
 * InventoryClickPreprocessor
 *
 * Preprocesses click packets, turning them into [InventoryClickAction] instances for further processing.
 *
 * @author TheFloodDragon
 * @since 2025/10/5 13:46
 */
object InventoryClickPreprocessor {

    /**
     * Processes the provided click packet, turning it into a [InventoryClickAction].
     *
     * @param packet the click packet
     */
    @JvmStatic
    fun processClick(packet: Packet, dragging: Supplier<PlayerDragging>): InventoryClickAction? {
        val clickType = packet.readOrThrow<Enum<*>>("clickType")
        val button = packet.readOrThrow<Number>("buttonNum").toInt()
        val slot = packet.readOrThrow<Number>("slotNum").toInt()

        val mode = try {
            Mode.valueOf(clickType.name)
        } catch (_: IllegalArgumentException) {
            Mode.entries[clickType.ordinal]
        }

        return if (slot != -999) {
            processValid(mode, slot, button, dragging)
        } else {
            processInvalid(mode, button, dragging)
        }
    }

    /**
     * Processes a click in a valid slot, possibly returning a result.
     */
    @JvmStatic
    private fun processValid(mode: Mode, slot: Int, button: Int, dragging: Supplier<PlayerDragging>): InventoryClickAction? {
        when (mode) {
            PICKUP -> when (button) {
                0 -> return MouseClick(slot, LEFT_CLICK)
                1 -> return MouseClick(slot, RIGHT_CLICK)
            }

            QUICK_MOVE -> when (button) {
                0 -> return MouseClick(slot, SHIFT_LEFT_CLICK)
                1 -> return MouseClick(slot, SHIFT_RIGHT_CLICK)
            }

            SWAP -> when (button) {
                in 0..<9 -> return HotbarSwap(button, slot)
                40 -> return OffhandSwap(slot)
            }

            CLONE -> return MouseClick(slot, MIDDLE_CLICK)

            THROW -> return Drop(slot, if (button == 1) CONTROL_DROP else DROP)

            QUICK_CRAFT -> {
                val slots: MutableSet<Int>
                val type: InventoryClickType
                // 拖动过程中
                when (button) {
                    1 -> {
                        slots = dragging.get().leftDrag
                        type = LEFT_MOUSE_DRAG_ADD_SLOT
                    }

                    5 -> {
                        slots = dragging.get().rightDrag
                        type = RIGHT_MOUSE_DRAG_ADD_SLOT
                    }

                    9 -> {
                        slots = dragging.get().middleDrag
                        type = MIDDLE_MOUSE_DRAG_ADD_SLOT
                    }

                    else -> return null
                }
                // 添加栏位
                slots.add(slot)
                // 返回动作
                return Drag(slots.toSet(), type) // toSet 复制一份
            }

            PICKUP_ALL -> return MouseClick(slot, DOUBLE_CLICK)
        }
        return null
    }

    /**
     * Processes a click in an invalid slot (i.e. the slot is irrelevant, like in a drop)
     */
    @JvmStatic
    private fun processInvalid(mode: Mode, button: Int, dragging: Supplier<PlayerDragging>): InventoryClickAction? {
        when (mode) {
            PICKUP, THROW -> when (button) {
                0 -> return Drop(-999, LEFT_CLICK_OUTSIDE)
                1 -> return Drop(-999, RIGHT_CLICK_OUTSIDE)
            }

            // 我也不知为啥会有这个, Minestom 的注解是: Why Mojang, why?
            CLONE -> if (button == 2) Drop(-999, MIDDLE_CLICK_OUTSIDE)

            QUICK_CRAFT -> {
                val slots: MutableSet<Int>
                val type: InventoryClickType
                when (button) {
                    // 拖动开始
                    0 -> {
                        slots = dragging.get().leftDrag
                        type = LEFT_MOUSE_DRAG_START
                    }

                    4 -> {
                        slots = dragging.get().rightDrag
                        type = RIGHT_MOUSE_DRAG_START
                    }

                    8 -> {
                        slots = dragging.get().middleDrag
                        type = MIDDLE_MOUSE_DRAG_START
                    }
                    // 拖动结束
                    2 -> {
                        slots = dragging.get().leftDrag
                        type = LEFT_MOUSE_DRAG_END
                    }

                    6 -> {
                        slots = dragging.get().rightDrag
                        type = RIGHT_MOUSE_DRAG_END
                    }

                    10 -> {
                        slots = dragging.get().middleDrag
                        type = MIDDLE_MOUSE_DRAG_END
                    }

                    else -> return null
                }
                // 创建拖动动作
                val action = Drag(slots.toSet(), type)
                // 清空拖动栏位列表
                slots.clear()
                // 返回拖动动作
                return action
            }

            else -> return null
        }
        return null
    }

    enum class Mode {
        PICKUP,
        QUICK_MOVE,
        SWAP,
        CLONE,
        THROW,
        QUICK_CRAFT,
        PICKUP_ALL;
    }

}

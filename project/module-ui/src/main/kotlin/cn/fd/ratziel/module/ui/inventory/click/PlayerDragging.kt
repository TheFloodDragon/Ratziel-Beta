package cn.fd.ratziel.module.ui.inventory.click

import java.util.*

/**
 * PlayerDragging
 *
 * @author TheFloodDragon
 * @since 2025/10/5 14:33
 */
class PlayerDragging {

    val leftDrag: MutableSet<Int> = linkedSetOf()
    val rightDrag: MutableSet<Int> = linkedSetOf()
    val middleDrag: MutableSet<Int> = linkedSetOf()

    companion object {

        @JvmStatic
        private val instances = hashMapOf<UUID, PlayerDragging>()

        /**
         * 获取玩家拖动实例
         */
        @JvmStatic
        operator fun get(uuid: UUID): PlayerDragging {
            return instances.computeIfAbsent(uuid) { PlayerDragging() }
        }

    }

}

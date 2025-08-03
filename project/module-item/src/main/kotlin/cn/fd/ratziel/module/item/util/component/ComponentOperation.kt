package cn.fd.ratziel.module.item.util.component

import cn.altawk.nbt.tag.NbtCompound
import cn.altawk.nbt.tag.NbtTag
import cn.altawk.nbt.tag.put
import cn.fd.ratziel.module.nbt.readInt

/**
 * ComponentOperation
 *
 * @author TheFloodDragon
 * @since 2025/8/3 11:54
 */
class ComponentOperation(
    /** 组件类型 **/
    val type: String,
    /** 组件操作 **/
    val operation: OperationType,
    /** 原始数据 **/
    val from: NbtTag?,
    /** 变化后数据 **/
    val to: NbtTag?,
) {

    fun unwarp() = NbtCompound {
        put(OPERATION_NAME, operation.ordinal)
        put(FROM_NAME, from)
        put(TO_NAME, to)
    }

    companion object {

        private const val OPERATION_NAME = "operation"
        private const val FROM_NAME = "from"
        private const val TO_NAME = "to"

        @JvmStatic
        fun parse(type: String, wrapped: NbtTag): ComponentOperation? {
            if (wrapped !is NbtCompound) return null
            val state = wrapped.readInt(OPERATION_NAME) ?: return null
            val from = wrapped[FROM_NAME]
            val to = wrapped[TO_NAME]
            return ComponentOperation(type, OperationType.entries[state], from, to)
        }

        @JvmStatic
        fun compareChanges(now: NbtCompound, before: NbtCompound): List<ComponentOperation> {
            // 标记变化
            val changes = ArrayList<ComponentOperation>()
            // 增改
            for (changed in now) {
                val origin = before[changed.key]
                val operation = if (origin == null) OperationType.ADD else OperationType.SET
                if (origin != changed.value) {
                    changes.add(ComponentOperation(changed.key, operation, origin, changed.value))
                }
            }
            // 删
            for (removed in before.filter { !now.containsKey(it.key) }) {
                changes.add(ComponentOperation(removed.key, OperationType.REMOVE, removed.value, null))
            }
            return changes
        }

    }

    enum class OperationType {
        /**
         * 组件被添加
         */
        ADD,

        /**
         * 组件被设置
         */
        SET,

        /**
         * 组件被移除
         */
        REMOVE,
    }

}
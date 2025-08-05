package cn.fd.ratziel.module.item.feature.virtual

import cn.altawk.nbt.tag.NbtCompound
import cn.altawk.nbt.tag.NbtTag
import cn.altawk.nbt.tag.put
import cn.altawk.nbt.tag.putCompound
import cn.fd.ratziel.module.nbt.readInt

/**
 * ComponentOperation
 *
 * @author TheFloodDragon
 * @since 2025/8/3 11:54
 */
class ComponentChange(
    /** 组件类型 **/
    val typeId: String,
    /** 组件操作 **/
    val operation: OperationType,
    /** 原始数据 **/
    val value: NbtTag?,
) {

    fun unwarp() = NbtCompound {
        putCompound(typeId) {
            put(OPERATION_NAME, operation.ordinal)
            if (value != null) put(VALUE_NAME, value)
        }
    }

    companion object {

        private const val OPERATION_NAME = "operation"
        private const val VALUE_NAME = "value"

        @JvmStatic
        fun parse(typeId: String, wrapped: NbtTag): ComponentChange? {
            if (wrapped !is NbtCompound) return null
            val state = wrapped.readInt(OPERATION_NAME) ?: return null
            val value = wrapped[VALUE_NAME] ?: return null
            return ComponentChange(typeId, OperationType.entries[state], value)
        }

        @JvmStatic
        fun compareChanges(now: NbtCompound, before: NbtCompound): List<ComponentChange> {
            // 标记变化
            val changes = ArrayList<ComponentChange>()
            // 增改
            for (changed in now) {
                val origin = before[changed.key]
                val operation = if (origin == null) OperationType.ADD else OperationType.SET
                if (origin != changed.value) {
                    changes.add(ComponentChange(changed.key, operation, origin))
                }
            }
            // 删
            for (removed in before.filter { !now.containsKey(it.key) }) {
                changes.add(ComponentChange(removed.key, OperationType.REMOVE, removed.value))
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
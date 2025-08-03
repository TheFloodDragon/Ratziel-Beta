package cn.fd.ratziel.module.item.feature.virtual

import cn.altawk.nbt.NbtPath
import cn.altawk.nbt.tag.NbtCompound
import cn.altawk.nbt.tag.put
import cn.fd.ratziel.core.functional.ArgumentContext
import cn.fd.ratziel.module.item.api.NeoItem
import cn.fd.ratziel.module.item.impl.RatzielItem
import cn.fd.ratziel.module.item.impl.SimpleMaterial
import cn.fd.ratziel.module.item.internal.ItemSheet
import cn.fd.ratziel.module.item.util.component.ComponentOperation
import cn.fd.ratziel.module.item.util.component.ComponentOperation.OperationType.*
import cn.fd.ratziel.module.nbt.handle
import cn.fd.ratziel.module.nbt.read
import cn.fd.ratziel.module.nbt.readString
import java.util.concurrent.CopyOnWriteArrayList

/**
 * NativeVirtualItemRenderer
 *
 * @author TheFloodDragon
 * @since 2025/8/3 11:43
 */
object NativeVirtualItemRenderer : VirtualItemRenderer {

    /** 虚拟物品数据节点 **/
    @JvmStatic
    private val VIRTUAL_PATH = RatzielItem.RATZIEL_PATH + NbtPath.NameNode("virtual")

    /** 变化记录节点 **/
    private const val CHANGES_NODE = "s2c_changes"

    /** 服务端材料节点 **/
    private const val SERVER_MATERIAL_NAME = "server_material"

    /** 客户端材料节点 **/
    private const val CLIENT_MATERIAL_NAME = "client_material"

    /**
     * 接收器表
     */
    val acceptors: MutableList<VirtualItemRenderer.Acceptor> = CopyOnWriteArrayList()

    override fun render(actual: NeoItem, context: ArgumentContext) {
        val before = actual.data.clone()

        // 接收器工作
        acceptors.forEach { it.accept(actual, context) }

        val now = actual.data
        // 自定义数据禁止修改
        before.tag[ItemSheet.CUSTOM_DATA_COMPONENT]?.also { now.tag.put(ItemSheet.CUSTOM_DATA_COMPONENT, it) }
        // 材质不能为空
        if (now.material.isEmpty()) now.material = before.material

        // 标记变化
        val changes = ComponentOperation.compareChanges(now.tag, before.tag)
        now.tag.handle(VIRTUAL_PATH) {
            // 清除之前的数据
            clear()
            // 记录修改的数据
            put(CHANGES_NODE, NbtCompound {
                changes.forEach {
                    mergeShallow(it.unwarp(), true)
                }
            })
            // 材质修改
            if (now.material != before.material) {
                put(SERVER_MATERIAL_NAME, before.material.name)
                put(CLIENT_MATERIAL_NAME, now.material.name)
            }
        }
    }

    override fun renderBySelf(actual: NeoItem) = applyChanges(actual, true)

    override fun recover(virtual: NeoItem) = applyChanges(virtual, false)

    private fun applyChanges(item: NeoItem, forward: Boolean) {
        val virtualData = item.data.tag.read(VIRTUAL_PATH) as? NbtCompound ?: return

        // 获取修改数据的记录
        val changes = (virtualData[CHANGES_NODE] as? NbtCompound)?.map {
            requireNotNull(ComponentOperation.parse(it.key, it.value)) {
                "Invalid component operation: ${it.key} = ${it.value}"
            }
        } ?: emptyList()

        for (change in changes) {
            // 改变化的 正/逆 向是删除还是设置
            val goingToSet = if (forward) {
                when (change.operation) {
                    ADD, SET -> true
                    REMOVE -> false
                }
            } else {
                when (change.operation) {
                    SET, REMOVE -> true
                    ADD -> false
                }
            }
            // 处理变化
            if (goingToSet) {
                val target = requireNotNull(if (forward) change.to else change.from) {
                    "Operation ${change.operation} with type '${change.type}' must have 'value' data!"
                }
                item.data.tag.put(change.type, target)
            } else {
                item.data.tag.remove(change.type)
            }
        }

        // 材质恢复
        val material = virtualData.readString(
            if (forward) CLIENT_MATERIAL_NAME else SERVER_MATERIAL_NAME
        )?.let { SimpleMaterial(it) }

        if (material != null && !material.isEmpty()) {
            item.data.material = material
        }
    }

}
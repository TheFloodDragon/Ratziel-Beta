package cn.fd.ratziel.module.item.feature.virtual

import cn.altawk.nbt.NbtPath
import cn.altawk.nbt.tag.NbtCompound
import cn.altawk.nbt.tag.put
import cn.fd.ratziel.core.functional.ArgumentContext
import cn.fd.ratziel.core.functional.SimpleContext
import cn.fd.ratziel.module.item.ItemManager
import cn.fd.ratziel.module.item.api.IdentifiedItem
import cn.fd.ratziel.module.item.api.NeoItem
import cn.fd.ratziel.module.item.feature.virtual.ComponentChange.OperationType.*
import cn.fd.ratziel.module.item.impl.RatzielItem
import cn.fd.ratziel.module.item.impl.SimpleMaterial
import cn.fd.ratziel.module.item.internal.ItemSheet
import cn.fd.ratziel.module.nbt.delete
import cn.fd.ratziel.module.nbt.handle
import cn.fd.ratziel.module.nbt.read
import cn.fd.ratziel.module.nbt.readString
import org.bukkit.entity.Player
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

    /**
     * 接收器表
     */
    val acceptors: MutableList<VirtualItemRenderer.Acceptor> = CopyOnWriteArrayList()

    fun render(actual: IdentifiedItem, player: Player) = this.render(actual, createContext(actual, player))

    override fun render(actual: NeoItem, context: ArgumentContext) {
        val before = actual.data.clone()

        // 接收器工作
        acceptors.forEach { it.accept(actual, context) }

        val now = actual.data
        // 自定义数据禁止修改
        val beforeCustomTag = before.tag[ItemSheet.CUSTOM_DATA_COMPONENT]
        if (beforeCustomTag == null) now.tag.remove(ItemSheet.CUSTOM_DATA_COMPONENT)
        else now.tag.put(ItemSheet.CUSTOM_DATA_COMPONENT, beforeCustomTag)
        // 材质不能为空
        if (now.material.isEmpty()) now.material = before.material

        // 标记变化
        val changes = ComponentChange.compareChanges(now.tag, before.tag)
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
            }
        }
    }

    override fun recover(virtual: NeoItem) {
        val virtualData = virtual.data.tag.read(VIRTUAL_PATH) as? NbtCompound ?: return
        // 删除虚拟数据, 因为服务端物品没有这个
        virtual.data.tag.delete(VIRTUAL_PATH)

        // 获取修改数据的记录
        val changes = (virtualData[CHANGES_NODE] as? NbtCompound)?.map {
            requireNotNull(ComponentChange.parse(it.key, it.value)) {
                "Invalid component operation: ${it.key} = ${it.value}"
            }
        } ?: emptyList()

        for (change in changes) {
            when (change.operation) {
                ADD -> virtual.data.tag.remove(change.typeId)
                SET, REMOVE -> {
                    val from = requireNotNull(change.value) {
                        "Operation ${change.operation} with typeId '${change.typeId}' must have 'value' data!"
                    }
                    virtual.data.tag.put(change.typeId, from)
                }
            }
        }

        // 材质恢复
        val material = virtualData.readString(SERVER_MATERIAL_NAME)?.let { SimpleMaterial(it) }

        if (material != null && !material.isEmpty()) {
            virtual.data.material = material
        }
    }

    /**
     * 创建上下文
     */
    fun createContext(item: IdentifiedItem, player: Player): SimpleContext {
        // 生成上下文
        val context = SimpleContext(item, player)
        // 导入生成器的上下文
        val generator = ItemManager.registry[item.identifier.content]
        val args = generator?.contextProvider?.newContext()?.args()
        if (args != null) context.putAll(args)
        return context
    }

}
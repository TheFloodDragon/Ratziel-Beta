package cn.fd.ratziel.module.item.feature.update

import cn.altawk.nbt.NbtPath
import cn.fd.ratziel.core.Identifier
import cn.fd.ratziel.core.contextual.SimpleContext
import cn.fd.ratziel.module.item.ItemManager
import cn.fd.ratziel.module.item.api.NeoItem
import cn.fd.ratziel.module.item.api.service.ItemServiceRegistry
import cn.fd.ratziel.module.item.impl.RatzielItem
import org.bukkit.entity.Player
import taboolib.common.platform.Awake
import taboolib.common.platform.function.warning
import java.util.concurrent.ConcurrentHashMap

/**
 * ItemUpdate
 *
 * @author TheFloodDragon
 * @since 2025/10/19 11:16
 */
class ItemUpdate(
    /**
     * 物品标识符
     */
    val identifier: Identifier,
) {

    /**
     * [UpdateInterpreter] 存储的物品更新的相关配置
     */
    val updater = ItemManager.generatorInterpreter<UpdateInterpreter>(identifier)

    /**
     * 更新是否启用
     */
    val enabled get() = updater.enabled

    /**
     * 物品当前版本
     */
    val version get() = updater.version

    /**
     * 受保护的 [NbtPath] 列表
     */
    val protectedPaths get() = updater.protectedPaths

    /**
     * 更新物品
     */
    fun update(item: RatzielItem.Sourced, player: Player): Boolean {
        val result = UpdateInterpreter.updateItem(item, SimpleContext(player))
            ?: return true // 空代表无需更新，直接返回 true
        // 获取更新完后的物品
        val updatedItem = result.handle { v, t ->
            if (t != null) Result.failure<NeoItem>(t)
            else Result.success(v)
        }.get().getOrElse {
            warning("Failed to update item $identifier:", it.stackTraceToString())
            return false // 此时只有失败了才会返回 null，代表物品更新失败
        } as RatzielItem
        // 强制写入源物品
        item.neoItem = updatedItem
        item.overwrite(true)
        return true
    }

    companion object {

        @JvmField
        val service = ConcurrentHashMap<Identifier, ItemUpdate>()

        @Awake
        private fun registerService() {
            // 注册服务
            ItemServiceRegistry.register(
                ItemUpdate::class.java,
                {
                    service.getOrPut(it) {
                        runCatching { ItemUpdate(it) }.getOrNull()
                    }
                },
                { k, v -> service[k] = v }
            )
        }

    }

}
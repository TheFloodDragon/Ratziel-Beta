package cn.fd.ratziel.module.item.feature.virtual

import cn.fd.ratziel.core.functional.SimpleContext
import cn.fd.ratziel.module.item.impl.RatzielItem
import cn.fd.ratziel.module.item.internal.nms.RefItemStack
import cn.fd.ratziel.platform.bukkit.util.readOrThrow
import com.google.common.cache.LoadingCache
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.component.TypedDataComponent
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.HashedPatchMap
import net.minecraft.network.HashedStack
import net.minecraft.resources.MinecraftKey
import net.minecraft.server.level.EntityPlayer
import net.minecraft.world.item.ItemStack
import org.bukkit.craftbukkit.v1_21_R4.entity.CraftPlayer
import taboolib.library.reflex.Reflex.Companion.getProperty
import taboolib.library.reflex.ReflexClass
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.Packet
import taboolib.module.nms.PacketReceiveEvent

/**
 * NMSVirtualItemImpl
 *
 * @author TheFloodDragon
 * @since 2025/8/3 23:54
 */
@Suppress("unused")
class NMSVirtualItemImpl : NMSVirtualItem() {

    private val carriedItemFieldInContainerClick = if (MinecraftVersion.isUniversal) "carriedItem" else "item"

    private val changedSlotsField = "changedSlots"

    private val containerSynchronizerField by lazy {
        ReflexClass.of(EntityPlayer::class.java, false).getField("containerSynchronizer", remap = true)
    }

    override fun handleContainerClick(event: PacketReceiveEvent) {
        val items = HashMap<Int, Any>()
        // 光标上的物品
        items[-10086] = event.packet.readOrThrow<Any>(carriedItemFieldInContainerClick)

        // 1.17 加入 changedSlots
        if (MinecraftVersion.isUniversal) {
            items.putAll(event.packet.readOrThrow<Map<Int, Any>>("changedSlots"))
        }

        if (MinecraftVersion.versionId >= 12105) handleModern(event, items) else handleLegacy(items)
    }

    private fun handleModern(event: PacketReceiveEvent, items: MutableMap<Int, Any>) {
        val serverPlayer = (event.player as CraftPlayer).handle
        val container = serverPlayer.containerMenu
        // 匿名内部类
        val cache = containerSynchronizerField.get(serverPlayer)
            ?.getProperty<LoadingCache<TypedDataComponent<*>, Int>>("cache") ?: return

        // 处理服务端物品 (不影响实际物品)
        val serverSideItems = items.mapNotNull {
            val actual = if (it.key == -10086) container.carried else container.getSlot(it.key).item
            prepareServerSide(actual)
        }
        // 尝试同步 Hash
        for (serverSide in serverSideItems) {
            // 逐个匹配
            for (clientItem in items) {
                val synced = syncIfMatchesMajority(clientItem.value, serverSide.first, serverSide.second, cache)
                if (synced != null) items[clientItem.key] = synced
            }
        }

        // 重新写入 items
        rewriteItems(event.packet, items)
    }

    private fun prepareServerSide(actual: Any): Pair<ItemStack, List<DataComponentType<*>>>? {
        // 判断是不是本插件的物品
        val customItem = ofCustomItem(actual) ?: return null

        // 仅标记渲染遍, 以便虚拟数据生成 (虚拟数据不包含客户端侧, 所以校验能过), 不渲染这个 custom_data 校验过不了
        NativeVirtualItemRenderer.render(customItem, SimpleContext(), true)

        // 获取排除的组件类型
        val excludes = NativeVirtualItemRenderer.readChangedTypes(customItem.data)
            .map { BuiltInRegistries.DATA_COMPONENT_TYPE.get(MinecraftKey.parse(it)).get().value() }

        // 渲染过的物品
        val rendered = RefItemStack.of(customItem.data).nmsStack as? ItemStack ?: return null
        return rendered to excludes
    }

    private fun syncIfMatchesMajority(
        stack: Any,
        serverItem: ItemStack,
        excludes: List<DataComponentType<*>>,
        cache: LoadingCache<TypedDataComponent<*>, Int>,
    ): HashedStack? {
        // HashedStack 就两种, 一种 Empty 一种 ActualStack, 空的就不处理了
        if (stack !is HashedStack.a) return null

        // 匹配物品
        val split = serverItem.componentsPatch.split()
        if (split.removed != stack.components.removedComponents) {
            return null
        } else if (stack.components.addedComponents.size != split.added.size()) {
            return null
        } else {
            // 要更新的 组件类型 - 哈希 表
            val newMap = HashMap<DataComponentType<*>, Int>()
            for (typed in split.added) {
                // 服务端存的哈希
                val serverHash = cache.get(typed)
                // 客户端发过来的哈希
                val clientHash = stack.components.addedComponents[typed.type]
                // 先设置为正确的哈希 (不匹配的话是整个方法返回空, 就不修改此 HashedStack)
                newMap[typed.type] = serverHash
                // 判断双端哈希 (同时排除动态修饰的)
                if (clientHash != serverHash && typed.type !in excludes) {
                    return null
                }
            }
            // 创建纠过的 HashedStack
            val syncedHashedMap = HashedPatchMap(newMap, stack.components.removedComponents)
            return HashedStack.a(stack.item, stack.count, syncedHashedMap)
        }
    }


    /**
     * 1.21.5-
     */
    fun handleLegacy(items: Map<Int, Any>) = runBlocking {
        items.map {
            // 直接恢复物品
            launch { NativeVirtualPacketHandler.handleItem(it.value) { v -> NativeVirtualItemRenderer.recover(v) } }
        }.joinAll()
    }

    /**
     * 重新写入 [items]
     */
    private fun rewriteItems(packet: Packet, items: Map<Int, Any>) {
        val carried = items[-10086]
        if (carried != null) {
            packet.write(carriedItemFieldInContainerClick, carried)
        }
        val slotItems = items.minus(-10086)
        if (slotItems.isNotEmpty()) {
            packet.write(changedSlotsField, Int2ObjectMaps.unmodifiable(Int2ObjectArrayMap(slotItems)))
        }
    }

    private fun ofCustomItem(nmsItem: Any): RatzielItem? {
        return RatzielItem.of(RefItemStack.ofNms(nmsItem).extractData())
    }

}
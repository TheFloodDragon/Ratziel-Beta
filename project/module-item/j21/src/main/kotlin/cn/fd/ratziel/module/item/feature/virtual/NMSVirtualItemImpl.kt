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

        // 尝试同步 Hash
        for (entry in items) {
            val actual = if (entry.key == -10086) container.carried else container.getSlot(entry.key).item
            val synced = syncIfMatchesMajority(entry.value, actual, cache)
            if (synced != null) items[entry.key] = synced
        }

        // 重新写入 items
        rewriteItems(event.packet, items)
    }

    private fun syncIfMatchesMajority(stack: Any?, actual: Any, cache: LoadingCache<TypedDataComponent<*>, Int>): HashedStack? {
        // HashedStack 就两种, 一种 Empty 一种 ActualStack, 空的就不处理了
        if (stack !is HashedStack.a) return null

        // 判断是不是本插件的物品
        val customItem = ofCustomItem(actual) ?: return null

        // 仅标记渲染遍, 以便虚拟数据生成 (虚拟数据不包含客户端侧, 所以校验能过), 不渲染这个 custom_data 校验过不了
        NativeVirtualItemRenderer.render(customItem, SimpleContext(), true)

        // 获取排除的组件类型
        val excludes = NativeVirtualItemRenderer.readChangedTypes(customItem.data)
            .map { BuiltInRegistries.DATA_COMPONENT_TYPE.get(MinecraftKey.parse(it)).get().value() }

        // 匹配物品
        val newMap = HashMap<DataComponentType<*>, Int>()
        var matches = true
        for (entry in stack.components.addedComponents) {
            @Suppress("UNCHECKED_CAST")
            val typed = TypedDataComponent(entry.key as DataComponentType<in Any>, entry.value)
            val cachedHash = cache.get(typed)
            newMap[entry.key] = cachedHash
            if ((entry.value != cachedHash) && entry.key !in excludes) {
                matches = false
                break
            }
        }
        return if (matches) {
            val syncedHashedMap = HashedPatchMap(newMap, stack.components.removedComponents)
            HashedStack.a(stack.item, stack.count, syncedHashedMap)
        } else null
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
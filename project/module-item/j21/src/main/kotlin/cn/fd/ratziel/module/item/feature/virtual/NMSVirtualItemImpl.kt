package cn.fd.ratziel.module.item.feature.virtual

import cn.fd.ratziel.core.functional.SimpleContext
import cn.fd.ratziel.module.item.impl.RatzielItem
import cn.fd.ratziel.module.item.internal.nms.RefItemStack
import cn.fd.ratziel.platform.bukkit.util.readOrThrow
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps
import net.minecraft.network.HashedPatchMap
import net.minecraft.network.HashedStack
import net.minecraft.world.item.ItemStack
import taboolib.module.nms.MinecraftVersion
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

    override fun handleContainerClick(event: PacketReceiveEvent) {
        val items = HashMap<Int, Any>()
        // 光标上的物品
        items[-10086] = event.packet.readOrThrow<Any>(carriedItemFieldInContainerClick)

        // 1.17 加入 changedSlots
        if (MinecraftVersion.isUniversal) {
            items.putAll(event.packet.readOrThrow<Map<Int, Any>>("changedSlots"))
        }

        if (MinecraftVersion.versionId >= 12105) {
            handleModern(event, items)
        } else {
            // 1.21.4-
            items.forEach { (_, item) ->
                // 直接恢复物品 (ItemStack在handleItem会被重写)
                NativeVirtualPacketHandler.handleItem(item) { NativeVirtualItemRenderer.recover(it) }
            }
        }
    }

    private fun handleModern(event: PacketReceiveEvent, items: MutableMap<Int, Any>) {
        // 替换成代理类
        val changedItems = Int2ObjectArrayMap<HashedStack>(items.size)
        for ((slot, value) in items) {
            if (slot == -10086) {
                val carried = ProxyHashedStack(value as HashedStack)
                event.packet.write(carriedItemFieldInContainerClick, carried)
            } else {
                changedItems.put(slot, ProxyHashedStack(value as HashedStack))
            }
        }
        event.packet.write(changedSlotsField, Int2ObjectMaps.unmodifiable(changedItems))
    }

    /**
     * 修改匹配逻辑的 [HashedStack] (不兼容其他任何用此方法的插件)
     */
    class ProxyHashedStack(val hashedStack: HashedStack) : HashedStack {

        override fun matches(serverItem: ItemStack, hashGenerator: HashedPatchMap.a): Boolean {
            // 数量不一样必须同步 (材料不要求)
            if (hashedStack is HashedStack.a && hashedStack.count != serverItem.count) return false
            var itemToMatch = serverItem

            // 判断是不是本插件的物品
            val customItem = asCustomItem(serverItem)
            if (customItem != null) {
                // 重新渲染遍, 以便虚拟数据生成 (虚拟数据不包含客户端侧, 所以校验能过), 不渲染这个校验过不了
                NativeVirtualItemRenderer.render(customItem, SimpleContext())
                // 设置要匹配的物品为渲染后的物品
                itemToMatch = RefItemStack.of(customItem.data).nmsStack as? ItemStack ?: return false
            }

            // 匹配 (动态修饰的部分不一样也要同步, 不然怎么刷新呢)
            return hashedStack.matches(itemToMatch, hashGenerator)
        }

        private fun asCustomItem(nmsItem: Any): RatzielItem? {
            return RatzielItem.of(RefItemStack.ofNms(nmsItem).extractData())
        }

    }

}
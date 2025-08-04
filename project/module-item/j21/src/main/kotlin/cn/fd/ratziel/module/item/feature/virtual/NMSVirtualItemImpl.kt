package cn.fd.ratziel.module.item.feature.virtual

import cn.fd.ratziel.core.functional.SimpleContext
import cn.fd.ratziel.module.item.impl.RatzielItem
import cn.fd.ratziel.module.item.internal.nms.RefItemStack
import cn.fd.ratziel.platform.bukkit.util.readOrThrow
import com.google.common.cache.LoadingCache
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps
import net.minecraft.core.component.DataComponents
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
        val serverPlayer = (event.player as CraftPlayer).handle
        // 匿名内部类
        val cache = containerSynchronizerField.get(serverPlayer)
            ?.getProperty<LoadingCache<TypedDataComponent<*>, Int>>("cache") ?: return

        // 替换成代理类
        val changedItems = Int2ObjectArrayMap<HashedStack>(items.size)
        for ((slot, value) in items) {
            if (slot == -10086 && value is HashedStack.a) {
                val carried = ProxyHashedStack(value, cache)
                event.packet.write(carriedItemFieldInContainerClick, carried)
            } else {
                if (value is HashedStack.a) {
                    changedItems.put(slot, ProxyHashedStack(value, cache))
                } else changedItems.put(slot, value as HashedStack)
            }
        }
        event.packet.write(changedSlotsField, Int2ObjectMaps.unmodifiable(changedItems))
    }

    /**
     * 修改匹配逻辑的 [HashedStack] (不兼容其他任何用此方法的插件)
     */
    class ProxyHashedStack(
        val stack: HashedStack.a,
        val cache: LoadingCache<TypedDataComponent<*>, Int>,
    ) : HashedStack {

        override fun matches(serverActual: ItemStack, hashGenerator: HashedPatchMap.a): Boolean {
            // 数量不一样必须同步 (材料不要求)
            if (stack.count != serverActual.count) return false
            // 判断是不是本插件的物品 (不是的话就转交逻辑处理权到 stack)
            val customItem = ofCustomItem(serverActual) ?: return stack.matches(serverActual, hashGenerator)

            // 仅标记渲染遍, 以便虚拟数据生成 (虚拟数据不包含客户端侧, 所以校验能过), 不渲染这个 custom_data 校验过不了
            NativeVirtualItemRenderer.render(customItem, SimpleContext(), true)
            // 获取排除的组件类型
            val excludes = NativeVirtualItemRenderer.readChangedTypes(customItem.data)
                .map { BuiltInRegistries.DATA_COMPONENT_TYPE.get(MinecraftKey.parse(it)).get().value() }
            // 渲染过的物品
            val rendered = RefItemStack.of(customItem.data).nmsStack as? ItemStack ?: return false

            // 匹配物品
            val split = rendered.componentsPatch.split()
            if (split.removed != stack.components.removedComponents // 确保删除的组件一致
                || stack.components.addedComponents.size != split.added.size() // 确保添加的组件一致
            ) {
                return false
            } else {
                for (typed in split.added) {
                    // 跳过排除的修饰了动态属性的类型 (profile是个人物)
                    if (typed.type in excludes || typed.type == DataComponents.PROFILE) continue
                    // 服务端存的哈希
                    val serverHash = cache.get(typed)
                    // 客户端发过来的哈希
                    val clientHash = stack.components.addedComponents[typed.type]
                    // 判断双端哈希
                    if (clientHash != serverHash) {
                        return false
                    }
                }
                // 匹配结果正确, 不同步
                return true
            }
        }

        private fun ofCustomItem(nmsItem: Any): RatzielItem? {
            return RatzielItem.of(RefItemStack.ofNms(nmsItem).extractData())
        }

    }

}
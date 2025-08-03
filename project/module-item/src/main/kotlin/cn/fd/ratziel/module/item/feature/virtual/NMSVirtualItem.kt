//package cn.fd.ratziel.module.item.feature.virtual
//
//import cn.fd.ratziel.module.item.feature.virtual.NativeVirtualPacketHandler.handleItem
//import cn.fd.ratziel.module.item.impl.RatzielItem
//import cn.fd.ratziel.module.item.internal.nms.NMSItem
//import cn.fd.ratziel.module.item.internal.nms.RefItemStack
//import cn.fd.ratziel.platform.bukkit.util.readOrThrow
//import com.google.common.cache.Cache
//import kotlinx.coroutines.joinAll
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.runBlocking
//import net.minecraft.core.component.DataComponentType
//import net.minecraft.core.component.TypedDataComponent
//import net.minecraft.core.registries.BuiltInRegistries
//import net.minecraft.network.HashedPatchMap
//import net.minecraft.network.HashedStack
//import net.minecraft.resources.MinecraftKey
//import net.minecraft.server.level.EntityPlayer
//import org.bukkit.craftbukkit.v1_21_R4.entity.CraftPlayer
//import taboolib.library.reflex.Reflex.Companion.getProperty
//import taboolib.library.reflex.ReflexClass
//import taboolib.module.nms.MinecraftVersion
//import taboolib.module.nms.PacketReceiveEvent
//import taboolib.module.nms.nmsProxy
//
///**
// * NMSVirtualItem
// *
// * @author TheFloodDragon
// * @since 2025/8/3 19:36
// */
//abstract class NMSVirtualItem {
//
//    abstract fun handleContainerClick(event: PacketReceiveEvent)
//
//    companion object {
//
//        val INSTANCE by lazy { nmsProxy<NMSItem>() }
//
//    }
//
//}
//
//class NMSVirtualItemImpl : NMSVirtualItem() {
//
//    val types by lazy {
//        NativeVirtualItemRenderer.acceptors.flatMap { it.changingTypes }
//            .map {
//                BuiltInRegistries.DATA_COMPONENT_TYPE.get(MinecraftKey.read(it).getOrThrow { s -> IllegalArgumentException(s) })
//            }
//    }
//
//    private fun handleModern(event: PacketReceiveEvent, carriedHash: HashedStack, changedHashes: Map<Int, HashedStack>) {
//        val serverPlayer = (event.player as CraftPlayer).handle
//        val container = serverPlayer.containerMenu
//        // 匿名内部类
//        val cache = containerSynchronizerField.get(serverPlayer)
//            ?.getProperty<Cache<TypedDataComponent<*>, Int>>("cache") ?: return
//
//        if (isRatzielItem(container.carried)) {
//            val handled = match(carriedHash, cache)
//            if (handled != null) {
//                event.packet.write(carriedItemFileInContainerClickPacket, handled)
//            }
//        }
//
//        for (changedHash in changedHashes) {
//            val changedHash = match(carriedHash, cache)
//            if (changedHash != null) {
//                event.packet.write(carriedItemFileInContainerClickPacket, handled)
//            }
//        }
//
//    }
//
//    private fun match(hashed: HashedStack, cache: Cache<TypedDataComponent<*>, Int>): HashedStack? {
//        val map = HashMap<DataComponentType<*>, Int>()
//
//        var matches = true
//        if (hashed !is HashedStack.a) return null
//        for (dc in hashed.components.addedComponents) {
//            val hash = cache.get(TypedDataComponent(dc.key, dc.value))
//            map.put(dc.key, hash)
//
//            if (dc.key in types) continue
//            if (dc.value != hash) {
//                matches = false
//                break
//            }
//        }
//        return if (matches) {
//            null
//        } else {
//            HashedStack.a(hashed.item, hashed.count, HashedPatchMap(map, hashed.components.removedComponents))
//        }
//    }
//
//    override fun handleContainerClick(event: PacketReceiveEvent) {
//        val items = ArrayList<Pair>()
//
//        val carriedItem = event.packet.readOrThrow<Any>(carriedItemFileInContainerClickPacket)
//        items.add(carriedItem)
//
//        if (MinecraftVersion.isUniversal) {
//            val changedSlots = event.packet.readOrThrow<Map<Int, Any>>("changedSlots")
//            items.addAll(changedSlots.values)
//        }
//
//        if (MinecraftVersion.versionId >= 12105) {
//            @Suppress("UNCHECKED_CAST")
//            handleModern(event, )
//        } else runBlocking {
//            items.map {
//                launch { handleItem(it) { v -> NativeVirtualItemRenderer.recover(v) } }
//            }.joinAll()
//        }
//    }
//
//    private fun isRatzielItem(nmsItem: Any): Boolean {
//        return RatzielItem.isRatzielItem(RefItemStack.ofNms(nmsItem).extractData())
//    }
//
//    private val carriedItemFileInContainerClickPacket = if (MinecraftVersion.isUniversal) "carriedItem" else "item"
//
//    private val containerSynchronizerField by lazy {
//        ReflexClass.of(EntityPlayer::class.java, false).getField("containerSynchronizer", remap = true)
//    }
//
//}
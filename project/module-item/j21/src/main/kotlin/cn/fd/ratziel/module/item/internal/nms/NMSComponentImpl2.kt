package cn.fd.ratziel.module.item.internal.nms

import cn.fd.ratziel.module.item.api.component.ItemComponentType
import cn.fd.ratziel.module.item.impl.component.ItemComponentData
import cn.fd.ratziel.module.item.impl.component.NamespacedIdentifier
import cn.fd.ratziel.module.item.internal.nms.CodecSerialization.modernOps
import cn.fd.ratziel.module.item.internal.nms.CodecSerialization.nmsOps
import cn.fd.ratziel.module.item.internal.nms.NMSItem.Companion.isModern
import com.google.common.collect.HashBiMap
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.NBTBase
import net.minecraft.resources.MinecraftKey
import taboolib.common.platform.function.severe
import kotlin.jvm.optionals.getOrNull
import net.minecraft.world.item.ItemStack as NMSItemStack

/**
 * NMSComponentImpl2
 * 
 * @author TheFloodDragon
 * @since 2025/11/29 21:43
 */
@Suppress("unused")
class NMSComponentImpl2 : NMSComponent() {

    val componentTypesBridge: HashBiMap<ItemComponentType<*>, DataComponentType<*>> = HashBiMap.create()

    fun <T : Any> getComponent(nmsItem: Any, type: ItemComponentType<T>): T? {
        val dct = type.dataComponentType
        val data = (nmsItem as NMSItemStack).componentsPatch.get(dct) ?: return null
        return type.transformer.transform(data.getOrNull() ?: return null)
    }

    fun <T : Any> setComponent(nmsItem: Any, type: ItemComponentType<T>, value: T) {
        val dct = type.dataComponentType
        val transformed = type.transformer.detransform(value)
        (nmsItem as NMSItemStack).set(dct, transformed)
    }

    override fun getComponent(nmsItem: Any, type: NamespacedIdentifier): ItemComponentData? {
        @Suppress("UNCHECKED_CAST")
        val dct = typeByName(type) as DataComponentType<Any>
        val data = (nmsItem as NMSItemStack).componentsPatch.get(dct) ?: return null
        val input = data.getOrNull() ?: return null
        // 返回数据
        return ItemComponentData.lazyGetter(data.isEmpty) {
            try {
                val result = if (isModern) {
                    dct.codecOrThrow().encodeStart(modernOps, input)
                } else {
                    dct.codecOrThrow().encodeStart(nmsOps, input).map {
                        NMSNbt.INSTANCE.fromNms(it)
                    }
                }
                result.getPartialOrThrow { error("Failed to save: $it") }
            } catch (ex: Throwable) {
                severe(ex.stackTraceToString()); null
            }
        }
    }

    override fun setComponent(nmsItem: Any, type: NamespacedIdentifier, data: ItemComponentData): Boolean {
        @Suppress("UNCHECKED_CAST")
        val dct = typeByName(type) as DataComponentType<Any>
        // 删除组件 (仅明确标记删除)
        if (data.removed) {
            (nmsItem as NMSItemStack).remove(dct)
        } else {
            // 标签不存在则不处理
            val input = data.tag ?: return false
            val value = try {
                val result = if (isModern) {
                    dct.codecOrThrow().parse(modernOps, input)
                } else {
                    dct.codecOrThrow().parse(nmsOps, NMSNbt.INSTANCE.toNms(input) as NBTBase)
                }
                result.getPartialOrThrow { error("Failed to save: $it") }
            } catch (ex: Throwable) {
                severe(ex.stackTraceToString()); return false
            }
            // 设置组件
            (nmsItem as NMSItemStack).set(dct, value)
        }
        return true
    }

    @Suppress("UNCHECKED_CAST")
    val ItemComponentType<*>.dataComponentType: DataComponentType<Any>
        get() = componentTypesBridge.computeIfAbsent(this) {
            val id = this.identifier
            val key = if (id is NamespacedIdentifier) {
                MinecraftKey.fromNamespaceAndPath(id.namespace, id.key)
            } else MinecraftKey.parse(id.content)
            // 从注册表中获取
            return@computeIfAbsent BuiltInRegistries.DATA_COMPONENT_TYPE.get(key)
                .getOrNull()?.value() ?: error("DataComponentType by '${key.path}' not found.")
        } as DataComponentType<Any>

    @Deprecated("Will be removed")
    fun typeByName(type: NamespacedIdentifier): DataComponentType<*> {
        val minecraftKey = MinecraftKey.fromNamespaceAndPath(type.namespace, type.key)
        return BuiltInRegistries.DATA_COMPONENT_TYPE.get(minecraftKey)
            .getOrNull()?.value() ?: error("DataComponentType by '${minecraftKey.path}' not found.")
    }

}
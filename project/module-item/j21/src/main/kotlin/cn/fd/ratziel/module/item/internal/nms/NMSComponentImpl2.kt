package cn.fd.ratziel.module.item.internal.nms

import cn.altawk.nbt.tag.NbtCompound
import cn.fd.ratziel.module.item.api.component.ItemComponentHolder
import cn.fd.ratziel.module.item.api.component.ItemComponentType
import cn.fd.ratziel.module.item.impl.component.CachedItemComponentHolder
import cn.fd.ratziel.module.item.impl.component.NamespacedIdentifier
import com.google.common.collect.HashBiMap
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.MinecraftKey
import net.minecraft.world.item.component.CustomData
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

    override fun createComponentHolder(nmsItem: Any): ItemComponentHolder {
        nmsItem as NMSItemStack
        return object : CachedItemComponentHolder<Any>() {
            override fun <T : Any> exchangeFromRaw(type: ItemComponentType<T>, raw: Any) = type.transformer.transform(raw)
            override fun <T : Any> exchangeToRaw(type: ItemComponentType<T>, value: T) = type.transformer.detransform(value)

            override fun getRaw(type: ItemComponentType<*>) = nmsItem.get(type.dataComponentType)

            override fun setRaw(type: ItemComponentType<*>, raw: Any?) {
                nmsItem.set(type.dataComponentType, raw)
            }

            override fun removeRaw(type: ItemComponentType<*>) {
                nmsItem.remove(type.dataComponentType)
            }
        }
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

    // some transformers

    override fun customDataComponentTransformer() = object : ItemComponentType.Transformer<NbtCompound> {

        override fun transform(src: Any): NbtCompound {
            src as CustomData
            @Suppress("DEPRECATION")
            return NMSNbt.INSTANCE.fromNms(src.unsafe) as NbtCompound
        }

        override fun detransform(tar: NbtCompound): Any {
            return NMSNbt.INSTANCE.toNms(tar)
        }

    }

}
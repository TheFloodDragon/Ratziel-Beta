package cn.fd.ratziel.module.item.internal.nms

import cn.altawk.nbt.tag.NbtCompound
import cn.fd.ratziel.core.Identifier
import cn.fd.ratziel.module.item.api.component.ItemComponentHolder
import cn.fd.ratziel.module.item.api.component.ItemComponentType
import cn.fd.ratziel.module.item.impl.component.CachedItemComponentHolder
import cn.fd.ratziel.module.item.impl.component.NamespacedIdentifier
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.MinecraftKey
import net.minecraft.world.item.component.CustomData
import java.util.concurrent.ConcurrentHashMap
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

    /**
     * [DataComponentType] 表
     */
    val nmsComponentTypesMap: MutableMap<NamespacedIdentifier, DataComponentType<*>> = ConcurrentHashMap()

    /**
     * 创建的 [ItemComponentType] 表
     */
    val unverifiedComponentTypesMap: MutableMap<NamespacedIdentifier, ItemComponentType<*>> = ConcurrentHashMap()

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

    override fun unverifiedComponentType(identifier: Identifier) = unverifiedComponentTypesMap.computeIfAbsent(identifier as NamespacedIdentifier) {
        val codec = identifier.dataComponentType.codecOrThrow()
        val serializer = CodecKSerializer(codec)
        ItemComponentType.Unverified(
            identifier,
            serializer,
            ItemComponentType.Transformer.NoTransformation()
        )
    }

    inline val ItemComponentType<*>.dataComponentType get() = this.identifier.dataComponentType

    @Suppress("UNCHECKED_CAST")
    val Identifier.dataComponentType: DataComponentType<Any>
        get() = nmsComponentTypesMap.computeIfAbsent(this as NamespacedIdentifier) {
            val key = MinecraftKey.fromNamespaceAndPath(this.namespace, this.key)
            // 从注册表中获取
            return@computeIfAbsent BuiltInRegistries.DATA_COMPONENT_TYPE.get(key)
                .getOrNull()?.value() ?: error("DataComponentType by '${key.path}' not found.")
        } as DataComponentType<Any>

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
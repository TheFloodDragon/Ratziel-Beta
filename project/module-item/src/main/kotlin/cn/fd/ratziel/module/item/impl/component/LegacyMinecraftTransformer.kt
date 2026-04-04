package cn.fd.ratziel.module.item.impl.component

import cn.fd.ratziel.module.item.api.component.transformer.MinecraftTransformer
import cn.fd.ratziel.module.item.api.component.transformer.NbtTransformer
import cn.fd.ratziel.module.item.internal.RefItemStack

/**
 * LegacyMinecraftTransformer
 *
 * 在旧版本中以 [RefItemStack] + [NbtTransformer] 适配出统一的 [MinecraftTransformer]。
 *
 * @author TheFloodDragon
 * @since 2026/3/22 00:00
 */
class LegacyMinecraftTransformer<T>(
    private val componentId: String,
    private val nbtTransformer: NbtTransformer<T>,
) : MinecraftTransformer<T> {

    override fun read(nmsItem: Any): T? {
        val ref = RefItemStack.ofNms(nmsItem)
        return nbtTransformer.readFrom(ref.tag)
    }

    override fun write(nmsItem: Any, component: T) {
        val ref = RefItemStack.ofNms(nmsItem)
        val root = ref.tag
        nbtTransformer.writeTo(root, component)
        ref.tag = root
    }

    override fun remove(nmsItem: Any) {
        val ref = RefItemStack.ofNms(nmsItem)
        val root = ref.tag
        nbtTransformer.removeFrom(root)
        ref.tag = root
    }

    override fun toString() = "LegacyMinecraftTransformer(componentId='$componentId', nbt=$nbtTransformer)"

}

package cn.fd.ratziel.module.item.impl.component.transformers

import cn.fd.ratziel.core.exception.UnsupportedVersionException
import cn.fd.ratziel.module.item.api.component.transformer.MinecraftTransformer
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.component.DataComponents
import net.minecraft.util.Unit
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.Unbreakable
import taboolib.module.nms.MinecraftVersion

/**
 * UnbreakableMinecraftTransformer
 *
 * @author TheFloodDragon
 * @since 2026/3/22 03:03
 */
@Suppress("unused")
class UnbreakableMinecraftTransformer : MinecraftTransformer<Boolean> {

    init {
        if (MinecraftVersion.versionId < 12005) throw UnsupportedVersionException()
    }

    override fun read(nmsItem: Any): Boolean {
        return (nmsItem as ItemStack).has(DataComponents.UNBREAKABLE)
    }

    override fun write(nmsItem: Any, component: Boolean) {
        nmsItem as ItemStack
        if (component) {
            if (MinecraftVersion.versionId >= 12105) {
                @Suppress("UNCHECKED_CAST")
                nmsItem.set(DataComponents.UNBREAKABLE as DataComponentType<Unit>, Unit.INSTANCE)
            } else {
                nmsItem.set(DataComponents.UNBREAKABLE, Unbreakable(true))
            }
        } else {
            nmsItem.remove(DataComponents.UNBREAKABLE)
        }
        return
    }

    override fun remove(nmsItem: Any) {
        (nmsItem as ItemStack).remove(DataComponents.UNBREAKABLE)
    }

}

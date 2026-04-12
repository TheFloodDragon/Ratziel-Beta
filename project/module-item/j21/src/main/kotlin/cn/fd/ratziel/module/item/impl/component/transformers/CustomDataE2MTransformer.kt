package cn.fd.ratziel.module.item.impl.component.transformers

import cn.altawk.nbt.tag.NbtCompound
import cn.fd.ratziel.module.item.impl.component.MinecraftE2MTransformer
import cn.fd.ratziel.module.item.internal.nms.NMSNbt
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.component.CustomData

/**
 * CustomDataE2MTransformer
 *
 * @author TheFloodDragon
 * @since 2026/3/21 23:55
 */
@Suppress("unused")
class CustomDataE2MTransformer : MinecraftE2MTransformer<NbtCompound> {

    override fun toMinecraftObj(encapsulated: NbtCompound): Any {
        val nmsTag = NMSNbt.INSTANCE.toNms(encapsulated)
        return CustomData.of(nmsTag as CompoundTag)
    }

    override fun fromMinecraftObj(minecraftObj: Any): NbtCompound {
        minecraftObj as CustomData
        @Suppress("DEPRECATION")
        val nmsTag = minecraftObj.copyTag()
        return NMSNbt.INSTANCE.fromNms(nmsTag) as NbtCompound
    }

}
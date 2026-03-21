package cn.fd.ratziel.module.item.impl.component.transformers

import cn.fd.ratziel.module.item.impl.component.MinecraftE2MTransformer
import net.kyori.adventure.text.Component
import net.minecraft.network.chat.IChatBaseComponent
import net.minecraft.world.item.component.ItemLore

/**
 * ItemLoreE2MTransformer
 * 
 * @author TheFloodDragon
 * @since 2026/3/21 23:41
 */
@Suppress("unused")
class ItemLoreE2MTransformer : MinecraftE2MTransformer<MutableList<Component>> {

    override fun toMinecraftObj(encapsulated: MutableList<Component>): Any {
        return ItemLore(encapsulated.map {
            MessageE2MTransformer.toMinecraftObj(it) as IChatBaseComponent
        })
    }

    override fun fromMinecraftObj(minecraftObj: Any): MutableList<Component> {
        minecraftObj as ItemLore
        return minecraftObj.lines.map {
            MessageE2MTransformer.fromMinecraftObj(it)
        }.toMutableList()
    }

}
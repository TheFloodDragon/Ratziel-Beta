package cn.fd.ratziel.module.item.impl.component.transformers

import cn.fd.ratziel.module.item.impl.component.MinecraftE2MTransformer
import cn.fd.ratziel.platform.bukkit.nms.NMSMessage
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
class ItemLoreE2MTransformer : MinecraftE2MTransformer<List<Component>> {

    override fun toMinecraftObj(encapsulated: List<Component>): Any {
        return ItemLore(encapsulated.map {
            NMSMessage.INSTANCE.toMinecraftComponent(it) as IChatBaseComponent
        })
    }

    override fun fromMinecraftObj(minecraftObj: Any): List<Component> {
        minecraftObj as ItemLore
        return minecraftObj.lines.map {
            NMSMessage.INSTANCE.fromMinecraftComponent(it)
        }
    }

}
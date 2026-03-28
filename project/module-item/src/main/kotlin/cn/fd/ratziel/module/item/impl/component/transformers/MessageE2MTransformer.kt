package cn.fd.ratziel.module.item.impl.component.transformers

import cn.fd.ratziel.module.item.impl.component.MinecraftE2MTransformer
import net.kyori.adventure.platform.bukkit.MinecraftComponentSerializer
import net.kyori.adventure.text.Component

/**
 * MessageE2MTransformer
 * 
 * @author TheFloodDragon
 * @since 2025/12/31 00:37
 */
@Suppress("UnstableApiUsage")
object MessageE2MTransformer : MinecraftE2MTransformer<Component> {

    override fun toMinecraftObj(encapsulated: Component): Any {
        return MinecraftComponentSerializer.get().serialize(encapsulated)
    }

    override fun fromMinecraftObj(minecraftObj: Any): Component {
        return MinecraftComponentSerializer.get().deserialize(minecraftObj)
    }

}
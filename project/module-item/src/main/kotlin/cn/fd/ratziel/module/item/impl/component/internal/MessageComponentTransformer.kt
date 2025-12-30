package cn.fd.ratziel.module.item.impl.component.internal

import cn.fd.ratziel.module.item.api.component.ItemComponentType
import net.kyori.adventure.platform.bukkit.MinecraftComponentSerializer
import net.kyori.adventure.text.Component

/**
 * MessageComponentTransformer
 * 
 * @author TheFloodDragon
 * @since 2025/12/31 00:37
 */
@Suppress("UnstableApiUsage")
object MessageComponentTransformer : ItemComponentType.Transformer<Component> {

    override fun transform(src: Any): Component {
        return MinecraftComponentSerializer.get().deserialize(src)
    }

    override fun detransform(tar: Component): Any {
        return MinecraftComponentSerializer.get().serialize(tar)
    }

}
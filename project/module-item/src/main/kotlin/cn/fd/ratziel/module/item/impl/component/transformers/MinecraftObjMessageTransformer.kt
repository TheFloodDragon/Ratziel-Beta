package cn.fd.ratziel.module.item.impl.component.transformers

import cn.fd.ratziel.module.item.api.component.transformer.MinecraftComponentTransformer
import net.kyori.adventure.platform.bukkit.MinecraftComponentSerializer
import net.kyori.adventure.text.Component

/**
 * MinecraftObjMessageTransformer
 * 
 * @author TheFloodDragon
 * @since 2025/12/31 00:37
 */
@Suppress("UnstableApiUsage")
object MinecraftObjMessageTransformer : MinecraftComponentTransformer<Component> {

    override fun transformToMinecraftObj(tar: Component): Any {
        return MinecraftComponentSerializer.get().serialize(tar)
    }

    override fun detransformFromMinecraftObj(src: Any): Component {
        return MinecraftComponentSerializer.get().deserialize(src)
    }

}
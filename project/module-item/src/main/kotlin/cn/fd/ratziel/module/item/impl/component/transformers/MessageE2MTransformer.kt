package cn.fd.ratziel.module.item.impl.component.transformers

import cn.fd.ratziel.module.item.impl.component.MinecraftE2MTransformer
import cn.fd.ratziel.platform.bukkit.nms.NMSMessage
import net.kyori.adventure.text.Component

/**
 * MessageE2MTransformer
 * 
 * @author TheFloodDragon
 * @since 2025/12/31 00:37
 */
object MessageE2MTransformer : MinecraftE2MTransformer<Component> {

    override fun toMinecraftObj(encapsulated: Component): Any {
        return NMSMessage.INSTANCE.toMinecraftComponent(encapsulated)
    }

    override fun fromMinecraftObj(minecraftObj: Any): Component {
        return NMSMessage.INSTANCE.fromMinecraftComponent(minecraftObj)
    }

}
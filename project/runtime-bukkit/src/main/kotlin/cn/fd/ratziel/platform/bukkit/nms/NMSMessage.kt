package cn.fd.ratziel.platform.bukkit.nms

import cn.fd.ratziel.common.message.Message
import net.kyori.adventure.text.Component
import taboolib.module.nms.MinecraftVersion.versionId
import taboolib.module.nms.nmsProxy

/**
 * NMSMessage
 * 
 * @author TheFloodDragon
 * @since 2026/3/29 00:13
 */
abstract class NMSMessage {

    abstract fun toMinecraftComponent(component: Component): Any

    abstract fun fromMinecraftComponent(minecraftComponent: Any): Component

    companion object {

        @JvmStatic
        val INSTANCE by lazy { nmsProxy<NMSMessage>() }

    }

}

@Suppress("unused")
class NMSMessageImpl : NMSMessage() {

    private typealias IChatBaseComponent12 = net.minecraft.server.v1_12_R1.IChatBaseComponent
    private typealias ChatSerializer12 = net.minecraft.server.v1_12_R1.IChatBaseComponent.ChatSerializer
    private typealias ChatSerializer16 = net.minecraft.server.v1_16_R1.IChatBaseComponent.ChatSerializer
    private typealias CraftChatMessage21 = org.bukkit.craftbukkit.v1_21_R3.util.CraftChatMessage
    private typealias IChatBaseComponent21 = net.minecraft.network.chat.IChatBaseComponent

    override fun toMinecraftComponent(component: Component): Any {
        return try {
            val jsonMessage = Message.transformToJson(component)
            if (versionId >= 11604) {
                CraftChatMessage21.fromJSON(jsonMessage)
            } else if (versionId >= 11600) {
                ChatSerializer16.a(jsonMessage)!!
            } else {
                ChatSerializer12.a(jsonMessage)!!
            }
        } catch (t: Throwable) {
            throw IllegalStateException("Got an error translating component! Please report!", t)
        }
    }

    override fun fromMinecraftComponent(minecraftComponent: Any): Component {
        val json = try {
            if (versionId >= 11604) {
                CraftChatMessage21.toJSON(minecraftComponent as IChatBaseComponent21)
            } else {
                ChatSerializer12.a(minecraftComponent as IChatBaseComponent12)!!
            }
        } catch (t: Throwable) {
            throw IllegalStateException("Got an error translating component!Please report!", t)
        }
        return Message.transformFromJson(json)
    }

}

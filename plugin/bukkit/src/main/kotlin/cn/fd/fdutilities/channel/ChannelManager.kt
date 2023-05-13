package cn.fd.fdutilities.channel

import cn.fd.fdutilities.FDUtilities
import com.google.common.io.ByteStreams
import org.bukkit.plugin.messaging.PluginMessageRecipient
import taboolib.common.platform.function.submit
import java.io.IOException

@Deprecated("未完成")
open class ChannelManager {

    //不可修改！
    val BUNGEE_CHANNEL = "BungeeCord"

    val FDUTILITIES_CHANNEL = "FDUtilities:Channel"

    val plugin by lazy { FDUtilities.plugin }

    fun sendCommonMessage(recipient: PluginMessageRecipient, vararg args: String, async: Boolean = false): Boolean {
        var success = true
        submit(async = async) {
            val out = ByteStreams.newDataOutput()

            try {
                for (arg in args) {
                    out.writeUTF(arg)
                }
            } catch (e: IOException) {
                e.printStackTrace()
                //e.print("Failed to send proxy common message!")
                success = false
            }

            recipient.sendPluginMessage(plugin, BUNGEE_CHANNEL, out.toByteArray())
        }
        return success
    }

    @Deprecated("未完成")
    fun sendFDUtilitiesMessage() {
    }

}
package cn.fd.fdutilities.channel

import com.google.common.io.ByteStreams
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener
import taboolib.common.platform.function.submit
import java.io.IOException

@Deprecated("未完成")
object BungeeChannel : ChannelManager(), PluginMessageListener {

    var Servers: ArrayList<String> = ArrayList()

    fun init() {

        if (!Bukkit.getMessenger().isOutgoingChannelRegistered(plugin, BUNGEE_CHANNEL)) {
            Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, BUNGEE_CHANNEL)
        }
        if (!Bukkit.getMessenger().isIncomingChannelRegistered(plugin, BUNGEE_CHANNEL)) {
            Bukkit.getMessenger().registerIncomingPluginChannel(plugin, BUNGEE_CHANNEL, this)
        }
        submit(period = 60, async = true) {
            sendCommonMessage(plugin.server, "GetServers")
        }
    }

    fun printServers() {
        if (Servers.isEmpty())
            sendCommonMessage(plugin.server, "GetServers")
        println(Servers)
    }

    override fun onPluginMessageReceived(channel: String, player: Player, message: ByteArray) {
        println("频道: $channel \n玩家: $player \n信息: $message")
        //如果为Bungee频道
        if (channel == BUNGEE_CHANNEL) {
            try {
                val data = ByteStreams.newDataInput(message)
                val subChannel = data.readUTF()

                println(subChannel)
                if (subChannel == "GetServers") {
                    val list: List<String> = data.readUTF().split(", ")
                    //Servers.clear()
                    for (server in list) {
                        println(server)
                        Servers.add(server)
                    }

                }
            } catch (_: IOException) {
            }
        }
        if (channel == FDUTILITIES_CHANNEL) {
            error("我就过来皮一下~")
        }

    }

}
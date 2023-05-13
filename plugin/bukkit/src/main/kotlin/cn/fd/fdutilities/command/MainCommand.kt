package cn.fd.fdutilities.command

import cn.fd.fdutilities.module.outdated.PlaceholderAPIExtension
import cn.fd.fdutilities.util.KetherParser.eval
import cn.fd.fdutilities.util.Loader
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.*
import taboolib.common.platform.function.adaptCommandSender
import taboolib.common.platform.function.info
import taboolib.common.platform.function.pluginVersion
import taboolib.common5.Coerce
import taboolib.module.kether.parseKetherScript
import taboolib.module.kether.runKether
import taboolib.module.lang.sendLang
import taboolib.platform.util.broadcast

@CommandHeader(
    "fdutilities",
    ["fd", "fdu"],
    "FDUtilities插件主命令",
    permission = "fdutilities.access",
    permissionDefault = PermissionDefault.TRUE
)
object MainCommand {

    //测试命令
    @CommandBody(permission = "fdutilities.command.test", permissionDefault = PermissionDefault.TRUE, optional = true)
    val test = subCommand {
        execute<CommandSender> { sender, _, argument ->
            val arg = argument.split("test")[1]
            if (sender is Player) {
                val player: Player = sender
                val msg = player.eval(arg).thenApply { Coerce.toString(it) }.get()
                player.sendMessage(msg)

                runKether {
                    arg.parseKetherScript().broadcast()
                    player.sendMessage(arg.parseKetherScript().toString())
                }

            }

            val mapList = PlaceholderAPIExtension.conf.getMapList("TestMap")
            info(mapList)
            for (map in mapList) {
                val c = map["condition"]
                info(map)
                info(c)
                if(sender is Player){
                    val player: Player = sender
                    val msg = player.eval(c.toString()).thenApply { Coerce.toString(it) }.get()
                    player.sendMessage(msg)

                }
            }


        }
    }

    /**
     * 帮助命令
     */
    @CommandBody(permission = "fdutilities.command.help", permissionDefault = PermissionDefault.TRUE, optional = true)
    val help = subCommand {
        execute<CommandSender> { sender, _, _ ->
            //显示帮助消息
            adaptCommandSender(sender).sendLang("Command-Help", pluginVersion)
        }
    }

    /**
     * 主命令
     */
    @CommandBody
    val main = mainCommand {
        execute<CommandSender> { sender, _, argument ->
            if (argument.isEmpty()) {
                //显示帮助消息
                adaptCommandSender(sender).sendLang("Command-Help", pluginVersion)
                return@execute
            }
        }
    }

    /**
     * 重载插件的命令
     */
    @CommandBody(permission = "fdutilities.command.reload", permissionDefault = PermissionDefault.OP, optional = true)
    val reload = subCommand {
        execute<ProxyCommandSender> { sender, _, _ ->
            //时间计算
            val before = System.currentTimeMillis()
            //重载部分
            Loader.reloadAll()
            //发送重载消息
            sender.sendLang("Plugin-Reload", System.currentTimeMillis() - before)
        }
    }

}
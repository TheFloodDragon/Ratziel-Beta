package cn.fd.utilities.bukkit.command

import cn.fd.utilities.bukkit.util.Loader
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.function.console
import taboolib.module.lang.sendLang

@Deprecated("测试用")
@PlatformSide([Platform.BUKKIT])
@CommandHeader(name = "fdutilities")
object MainCommand {

    /**
     * 重载插件的命令
     */
    @CommandBody
    val main = subCommand {
        //时间计算
        val before = System.currentTimeMillis()
        //重载部分
        Loader.reloadAll()
        //发送重载消息
        console().sendLang("Plugin-Reload", System.currentTimeMillis() - before)
    }
//
//    //测试命令
//    @CommandBody(permission = "fdutilities.command.test", permissionDefault = PermissionDefault.TRUE, optional = true)
//    val test = subCommand {
//        execute<CommandSender> { sender, _, argument ->
//            val arg = argument.split("test")[1]
//            if (sender is Player) {
//                val player: Player = sender
//                val msg = player.eval(arg).thenApply { Coerce.toString(it) }.get()
//                player.sendMessage(msg)
//
//                runKether {
//                    arg.parseKetherScript().broadcast()
//                    player.sendMessage(arg.parseKetherScript().toString())
//                }
//
//            }
//
//            //val mapList = PlaceholderAPIExtension.conf.getMapList("TestMap")
//            //info(mapList)
////            for (map in mapList) {
////                val c = map["condition"]
////                info(map)
////                info(c)
////                if (sender is Player) {
////                    val player: Player = sender
////                    val msg = player.eval(c.toString()).thenApply { Coerce.toString(it) }.get()
////                    player.sendMessage(msg)
////
////                }
////            }
//
//
//        }
//    }
//
//    /**
//     * 帮助命令
//     */
//    @CommandBody(permission = "fdutilities.command.help", permissionDefault = PermissionDefault.TRUE, optional = true)
//    val help = subCommand {
//        execute<CommandSender> { sender, _, _ ->
//            //显示帮助消息
//            adaptCommandSender(sender).sendLang("Command-Help", pluginVersion)
//        }
//    }
//
//    /**
//     * 主命令
//     */
//    @CommandBody
//    val main = mainCommand {
//        execute<CommandSender> { sender, _, argument ->
//            if (argument.isEmpty()) {
//                //显示帮助消息
//                adaptCommandSender(sender).sendLang("Command-Help", pluginVersion)
//                return@execute
//            }
//        }
//    }


}
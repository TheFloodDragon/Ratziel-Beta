package cn.fd.utilities.bukkit.command

import cn.fd.utilities.common.util.debug
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand

@PlatformSide([Platform.BUKKIT])
@CommandHeader(name = "fdutilities")
object MainCommand {

    /**
     * ÖØÔØ²å¼şµÄÃüÁî
     */
    @CommandBody
    val main = mainCommand {
    }

    //²âÊÔÃüÁî
    @CommandBody
    val test = subCommand {
        debug("²âÊÔ")
    }

}
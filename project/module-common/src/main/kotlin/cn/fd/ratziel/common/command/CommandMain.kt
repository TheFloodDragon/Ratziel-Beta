package cn.fd.ratziel.common.command

import cn.fd.ratziel.common.WorkspaceLoader
import cn.fd.ratziel.common.config.Settings
import taboolib.common.platform.PlatformFactory
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.*
import taboolib.expansion.createHelper
import taboolib.library.reflex.ReflexClass
import taboolib.module.lang.Language
import taboolib.module.lang.sendLang
import kotlin.system.measureTimeMillis

/**
 * CommandMain
 *
 * @author TheFloodDragon
 * @since 2025/10/11 21:50
 */
@CommandHeader(
    name = "ratziel",
    aliases = ["r", "rz", "f"],
    permission = "ratziel.command.main",
    description = "Main Command"
)
object CommandMain {

    @CommandBody
    val main = mainCommand { createHelper() }

    @CommandBody
    val element = CommandElement

    @CommandBody
    val reload = subCommand {
        executeAsync<ProxyCommandSender> { sender, _, _ ->
            measureTimeMillis {
                // 重载配置
                Settings.conf.reload()
                // 重载语言
                Language.reload()
                // 重载工作空间
                WorkspaceLoader.reload(sender)
            }.let {
                sender.sendLang("Plugin-Reloaded", it)
            }
        }
    }

    /**
     * [SimpleCommandRegister]
     */
    val commandRegister: SimpleCommandRegister by lazy {
        PlatformFactory.getAPI<SimpleCommandRegister>()
    }

    /**
     * 注册子命令
     */
    fun registerSubCommand(body: SimpleCommandBody) {
        commandRegister.body[CommandMain::class.java.name]!!.add(body)
    }

    /**
     * 注册子命令
     */
    fun registerSubCommand(clazz: Class<*>, commandName: String) {
        val owner = ReflexClass.of(clazz, false)
        val body = SimpleCommandBody().apply {
            this.name = commandName
            owner.structure.fields.forEach {
                children += commandRegister.loadBody(it, owner) ?: return@forEach
            }
        }
        this.registerSubCommand(body)
    }

}

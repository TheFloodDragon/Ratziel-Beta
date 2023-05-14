package cn.fd.utilities

import cn.fd.utilities.channel.BungeeChannel
import cn.fd.utilities.config.ConfigYaml
import cn.fd.utilities.module.outdated.Extension
import cn.fd.utilities.util.Adventure
import cn.fd.utilities.util.FileListener
import cn.fd.utilities.util.Loader
import org.bukkit.Bukkit
import taboolib.common.env.RuntimeEnv
import taboolib.common.platform.Awake
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.console
import taboolib.common.platform.function.pluginVersion
import taboolib.module.lang.Language
import taboolib.module.lang.sendLang
import taboolib.platform.BukkitPlugin

@PlatformSide([Platform.BUKKIT])
object FDUtilities : Plugin() {

    val plugin by lazy { BukkitPlugin.getInstance() }

    /**
     * 加载依赖项
     */
    @Awake
    fun loadDependency() {
        RuntimeEnv.ENV.loadDependency(CommonEnv::class.java, true)
    }

    override fun onLoad() {
        BungeeChannel.init()
        //插件语言设置
        Language.default = ConfigYaml.LANGUAGE
        //加载模块配置文件
        Loader.reloadAll()
        console().sendLang("Plugin-Loading", Bukkit.getBukkitVersion())
    }

    override fun onEnable() {
        //开启并创建 BukkitAudiences
        Adventure.enable()

        Extension.printInfo()
        console().sendLang("Plugin-Enabled", pluginVersion)
    }

    override fun onDisable() {
        //关闭 BukkitAudiences
        Adventure.disable()
        //关闭配置文件监听器
        FileListener.uninstall()
    }

}